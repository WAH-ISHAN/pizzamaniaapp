package com.pizzamania.data.repo

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.pizzamania.data.local.MenuCacheDao
import com.pizzamania.data.local.MenuCacheEntity
import com.pizzamania.data.model.MenuItem
import kotlinx.coroutines.tasks.await

class MenuRepo(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val cache: MenuCacheDao
) {

    /** 🔹 Add or Update menu item */
    suspend fun addOrUpdateMenu(
        branchId: String,
        itemId: String?,
        name: String,
        desc: String,
        price: Double,
        stock: Long,
        imageUri: Uri?,
        toppings: List<String>
    ) = runCatching {
        val docRef = if (itemId != null) {
            db.collection("branches").document(branchId)
                .collection("menus").document(itemId)
        } else {
            db.collection("branches").document(branchId)
                .collection("menus").document()
        }

        var imageUrl: String? = null
        if (imageUri != null) {
            val storageRef = storage.reference
                .child("menus/$branchId/${docRef.id}.jpg")
            storageRef.putFile(imageUri).await()
            imageUrl = storageRef.downloadUrl.await().toString()
        }

        // Firestore document data
        val data = hashMapOf<String, Any?>(
            "name" to name,
            "description" to desc,
            "price" to price,
            "stock" to stock,
            "updatedAt" to System.currentTimeMillis(),
            "toppings" to toppings
        )
        if (imageUrl != null || itemId == null) {
            data["imageUrl"] = imageUrl
        }

        // Save to Firestore
        docRef.set(data, SetOptions.merge()).await()

        // Save to local Room cache
        cache.insertOrUpdate(
            MenuCacheEntity(
                id = docRef.id,
                branchId = branchId,
                name = name,
                description = desc,
                price = price,
                imageUrl = imageUrl,
                stock = stock,
                toppings = toppings
            )
        )
    }

    /** 🔹 Delete menu item */
    suspend fun deleteMenu(branchId: String, itemId: String) = runCatching {
        // Delete from Firestore
        db.collection("branches").document(branchId)
            .collection("menus").document(itemId)
            .delete().await()

        // Delete associated image
        val imageRef = storage.reference.child("menus/$branchId/$itemId.jpg")
        try { imageRef.delete().await() } catch (_: Exception) {}

        // Delete local Room cache entry
        cache.deleteById(itemId)
    }

    /** 🔹 Sync all menus for a branch (from Firestore → update cache) */
    suspend fun syncMenus(branchId: String): List<MenuItem> {
        return try {
            val snapshot = db.collection("branches")
                .document(branchId)
                .collection("menus")
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    MenuItem(
                        id = doc.id,
                        branchId = branchId,
                        name = doc.getString("name") ?: "Unnamed Pizza",
                        description = doc.getString("description") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        imageUrl = doc.getString("imageUrl"),
                        stock = doc.getLong("stock") ?: 0L,
                        toppings = doc.get("toppings") as? List<String> ?: emptyList()
                    )
                } catch (_: Exception) {
                    null
                }
            }

            // Update local cache
            items.forEach { item ->
                cache.insertOrUpdate(
                    MenuCacheEntity(
                        id = item.id,
                        branchId = item.branchId,
                        name = item.name,
                        description = item.description,
                        price = item.price,
                        imageUrl = item.imageUrl,
                        stock = item.stock,
                        toppings = item.toppings   // ✅ FIXED
                    )
                )
            }

            items
        } catch (e: Exception) {
            // If Firestore fails → load from cache
            cache.byBranch(branchId).map {
                MenuItem(
                    id = it.id,
                    branchId = it.branchId,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    stock = it.stock,
                    toppings = it.toppings
                )
            }
        }
    }

    /** 🔹 Load menus offline (only from cache) */
    suspend fun cachedMenus(branchId: String): List<MenuItem> =
        cache.byBranch(branchId).map {
            MenuItem(
                id = it.id,
                branchId = it.branchId,
                name = it.name,
                description = it.description,
                price = it.price,
                imageUrl = it.imageUrl,
                stock = it.stock,
                toppings = it.toppings
            )
        }
}