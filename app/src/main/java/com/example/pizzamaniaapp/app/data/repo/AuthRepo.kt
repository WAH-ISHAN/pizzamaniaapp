package com.pizzamania.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepo(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    suspend fun registerAdmin(email: String, pass: String, branchId: String) = runCatching {
        val cred = auth.createUserWithEmailAndPassword(email, pass).await()
        val uid = cred.user!!.uid
        db.collection("users").document(uid).set(
            mapOf("email" to email, "role" to "admin", "branchId" to branchId)
        ).await()
    }

    suspend fun registerUser(name: String, email: String, pass: String) = runCatching {
        val cred = auth.createUserWithEmailAndPassword(email, pass).await()
        val uid = cred.user!!.uid
        db.collection("users").document(uid).set(
            mapOf("email" to email, "name" to name, "role" to "user")
        ).await()
    }

    suspend fun signIn(email: String, pass: String) = runCatching {
        auth.signInWithEmailAndPassword(email, pass).await()
        auth.currentUser!!.uid
    }

    suspend fun currentRoleBranch(): Pair<String, String?> {
        val uid = auth.currentUser?.uid ?: return "guest" to null
        val snap = db.collection("users").document(uid).get().await()
        return (snap.getString("role") ?: "user") to snap.getString("branchId")
    }
}
