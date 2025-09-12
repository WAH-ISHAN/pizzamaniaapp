package com.example.pizzamaniaapp.data.repo

import  android.net.Uri
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
        val docRef = if (itemId == null)
            db.collection("branches").document(branchId).collection("menus").document()
        else
            db.collection("branches").document(branchId).collection("menus").document(itemId)



        var imageUrl: String? = null
        if (imageUri != null) {
            val ref = storage.reference.child("menus/$branchId/${docRef.id}.jpg")
            ref.putFile(imageUri).await()
            imageUrl = ref.downloadUrl.await().toString()
        }
        val data = mutableMapOf<String, Any>(
            "name" to name, "description" to desc, "price" to price, "stock" to stock
        )
        if (imageUrl != null) data["imageUrl"] = imageUrl
        docRef.set(data, SetOptions.merge()).await()
    }

    suspend fun deleteMenu(branchId: String, itemId: String) = runCatching {
        db.collection("branches").document(branchId).collection("menus").document(itemId).delete().await()
    }

    suspend fun syncMenus(branchId: String): List<MenuItem> {
        val snaps = db.collection("branches").document(branchId).collection("menus").get().await()
        val list = snaps.documents.map {
            MenuItem(
                id = it.id, branchId = branchId,
                name = it.getString("name")!!,
                description = it.getString("description") ?: "",
                price = it.getDouble("price") ?: 0.0,
                imageUrl = it.getString("imageUrl"),
                stock = (it.getLong("stock") ?: 0L)
            )
        }
        cache.clearBranch(branchId)
        cache.upsertAll(list.map { m -> MenuCacheEntity(m.id, m.branchId, m.name, m.description, m.price, m.imageUrl, m.stock) })
        return list
    }

    suspend fun cachedMenus(branchId: String): List<MenuItem> =
        cache.byBranch(branchId).map { MenuItem(it.id, it.branchId, it.name, it.description, it.price, it.imageUrl, it.stock) }
}