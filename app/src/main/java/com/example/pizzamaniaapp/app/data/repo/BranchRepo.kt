package com.pizzamania.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.pizzamania.data.model.Branch
import kotlinx.coroutines.tasks.await

class BranchRepo(private val db: FirebaseFirestore) {
    suspend fun getBranches(): List<Branch> {
        val snaps = db.collection("branches").get().await()
        return snaps.documents.map {
            Branch(
                id = it.id,
                name = it.getString("name")!!,
                lat = it.getDouble("lat")!!,
                lng = it.getDouble("lng")!!,
                address = it.getString("address") ?: ""
            )
        }
    }
}
