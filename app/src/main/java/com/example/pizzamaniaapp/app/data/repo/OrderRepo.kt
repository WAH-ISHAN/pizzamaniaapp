package com.example.pizzamaniaapp.app.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.pizzamania.data.local.CartItemEntity
import com.pizzamania.data.repo.BranchRepo
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class OrderRepo(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val branches: BranchRepo
) {

    /** Haversine formula to compute distance between two lat/lng points (km) */
    private fun distanceKm(aLat: Double, aLng: Double, bLat: Double, bLng: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(bLat - aLat)
        val dLng = Math.toRadians(bLng - aLng)
        val s1 = sin(dLat / 2).pow(2.0)
        val s2 = cos(Math.toRadians(aLat)) * cos(Math.toRadians(bLat)) * sin(dLng / 2).pow(2.0)
        return 2 * R * asin(sqrt(s1 + s2))
    }

    /** Checks if branch has stock for all given items */
    private suspend fun hasStockForAll(branchId: String, cart: List<CartItemEntity>): Boolean {
        val ids = cart.map { it.menuId }.distinct()
        if (ids.isEmpty()) return false
        val snaps = db.collection("branches")
            .document(branchId)
            .collection("menus")
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .await()

        val map = snaps.documents.associate { it.id to (it.getLong("stock") ?: 0L) }
        return cart.all { (map[it.menuId] ?: 0L) >= it.qty }
    }

    /**
     * Places an order:
     * - Picks nearest branch with available stock
     * - Deducts stock transactionally
     * - Creates order doc in Firestore
     */
    suspend fun placeOrder(
        address: String,
        phone: String,
        payment: String,
        userLat: Double?,
        userLng: Double?,
        cart: List<CartItemEntity>
    ): Result<String> = runCatching {
        val uid = auth.currentUser?.uid ?: error("Not signed in")

        // pick branch list sorted by distance (if location given)
        val branchesList = branches.getBranches()
        val orderedBranches =
            if (userLat != null && userLng != null)
                branchesList.sortedBy { distanceKm(userLat, userLng, it.lat, it.lng) }
            else branchesList

        // choose the first branch that has stock, else default first
        val chosenBranch = orderedBranches.firstOrNull { hasStockForAll(it.id, cart) }
            ?: orderedBranches.firstOrNull()
            ?: error("No branches available")

        // target order doc
        val orderRef = db.collection("orders").document()

        // Transaction: check stock, deduct, create order
        db.runTransaction { tr ->
            cart.forEach { ci ->
                val menuRef = db.collection("branches")
                    .document(chosenBranch.id)
                    .collection("menus")
                    .document(ci.menuId)

                val snap = tr.get(menuRef)
                val stock = snap.getLong("stock") ?: 0L
                if (stock < ci.qty) {
                    throw FirebaseFirestoreException(
                        "Out of stock: ${ci.name}",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }
                tr.update(menuRef, "stock", stock - ci.qty)
            }

            val total = cart.sumOf { it.price * it.qty }
            val items = cart.map {
                mapOf(
                    "menuId" to it.menuId,
                    "name" to it.name,
                    "price" to it.price,
                    "qty" to it.qty
                )
            }

            val data = mapOf(
                "userId" to uid,
                "branchId" to chosenBranch.id,
                "items" to items,
                "total" to total,
                "address" to address,
                "phone" to phone,
                "payment" to payment,
                "status" to "Pending",
                // let Firestore insert the server timestamp
                "createdAt" to FieldValue.serverTimestamp()
            )

            tr.set(orderRef, data)
        }.await()

        // return id so UI can show "Order placed #id"
        orderRef.id
    }
}