package com.example.pizzamaniaapp.app.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

suspend fun seedBranches() {
    val db = Firebase.firestore
    val existing = db.collection("branches").get().await()
    if (!existing.isEmpty) return
    val data = listOf(
        mapOf("name" to "Colombo", "address" to "Colombo 01", "lat" to 6.9271, "lng" to 79.8612),
        mapOf("name" to "Galle", "address" to "Galle Fort", "lat" to 6.0535, "lng" to 80.2210)
    )
    data.forEach { db.collection("branches").add(it).await() }
}