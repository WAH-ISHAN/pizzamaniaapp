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
    suspend fun addOrUpdateMenu(
        branchId: String,
        itemId: String?,
        name: String,
        desc: String,
        price: Double,
        stock: Long,
        imageUri: Uri?
    ) = runCatching {
        // ... existing implementation
    }

    suspend fun deleteMenu(branchId: String, itemId: String) = runCatching {
        // ... existing implementation
    }

    suspend fun syncMenus(branchId: String): List<MenuItem> {
        // ... existing implementation
        return TODO("Provide the return value")
    }

    suspend fun cachedMenus(branchId: String): List<MenuItem> =
        cache.byBranch(branchId).map {
            MenuItem(it.id, it.branchId, it.name, it.description, it.price, it.imageUrl, it.stock)
        }
}