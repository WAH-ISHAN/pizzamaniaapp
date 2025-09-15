package com.pizzamania.data.repo

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
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

    suspend fun addOrUpdateMenu(
        branchId: String,
        itemId: String?,
        name: String,
        desc: String,
        price: Double,
        stock: Long,
        imageUri: Uri?
    ) = runCatching {
        // 🔹TODO: Handle upload image and save/update Firestore Document
        // Example (sketched, you should implement):
        // 1. Upload image -> storage.reference.child("menus/$branchId/$generatedId.jpg").putFile(imageUri).await()
        // 2. Save Firestore document with new data
    }

    suspend fun deleteMenu(branchId: String, itemId: String) = runCatching {
        // 🔹TODO: Delete from Firestore and Cache as needed
    }

    /**
     * 🔹 Fetch Menus from Firestore
     */
    suspend fun syncMenus(branchId: String): List<MenuItem> {
        return try {
            val snapshot = db.collection("branches")
                .document(branchId)
                .collection("menus")
                .get()
                .await()

            // Map Firestore docs -> MenuItem
            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    MenuItem(
                        id = doc.id,
                        branchId = branchId,
                        name = doc.getString("name") ?: "Unnamed Pizza",
                        description = doc.getString("description") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        imageUrl = doc.getString("imageUrl"),
                        stock = doc.getLong("stock") ?: 0L
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

            // Update cache (cannot do inside mapNotNull, so we loop after)
            items.forEach { item ->
                try {
                    cache.insertOrUpdate(
                        MenuCacheEntity(
                            id = item.id,
                            branchId = item.branchId,
                            name = item.name,
                            description = item.description,
                            price = item.price,
                            imageUrl = item.imageUrl,
                            stock = item.stock
                        )
                    )
                } catch (_: Exception) {
                    // ignore single insert failures
                }
            }

            items
        } catch (e: Exception) {
            e.printStackTrace()
            // fallback → local cache
            cache.byBranch(branchId).map {
                MenuItem(
                    id = it.id,
                    branchId = it.branchId,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    stock = it.stock
                )
            }
        }
    }

    /**
     * 🔹 Fetch cached menus (offline support)
     */
    suspend fun cachedMenus(branchId: String): List<MenuItem> =
        cache.byBranch(branchId).map {
            MenuItem(
                id = it.id,
                branchId = it.branchId,
                name = it.name,
                description = it.description,
                price = it.price,
                imageUrl = it.imageUrl,
                stock = it.stock
            )
        }
}