package com.pizzamania.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Data model for Branch
data class Branch(
    val id: String,          // Firestore document id
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double
)

class BranchRepo(private val db: FirebaseFirestore) {

    /** Fetch all branches from Firestore */
    suspend fun getBranches(): List<Branch> {
        val snapshot = db.collection("branches").get().await()

        return snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("name") ?: return@mapNotNull null
            val lat = doc.getDouble("lat") ?: return@mapNotNull null
            val lng = doc.getDouble("lng") ?: return@mapNotNull null
            val address = doc.getString("address") ?: ""

            Branch(
                id = doc.id,
                name = name,
                address = address,
                lat = lat,
                lng = lng
            )
        }
    }
}