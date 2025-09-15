package com.pizzamania.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepo(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    /**
     * Register Admin and return UID
     */
    suspend fun registerAdmin(email: String, pass: String, branchId: String): Result<String> = runCatching {
        val cred = auth.createUserWithEmailAndPassword(email, pass).await()
        val uid = cred.user?.uid ?: throw IllegalStateException("UID missing")

        db.collection("users").document(uid).set(
            mapOf(
                "email" to email,
                "role" to "admin",
                "branchId" to branchId
            )
        ).await()

        uid // return uid on success
    }

    /**
     * Register User and return UID
     */
    suspend fun registerUser(name: String, email: String, pass: String): Result<String> = runCatching {
        val cred = auth.createUserWithEmailAndPassword(email, pass).await()
        val uid = cred.user?.uid ?: throw IllegalStateException("UID missing")

        db.collection("users").document(uid).set(
            mapOf(
                "email" to email,
                "name" to name,
                "role" to "user"
            )
        ).await()

        uid // return uid on success
    }

    /**
     * Sign in and return UID
     */
    suspend fun signIn(email: String, pass: String): Result<String> = runCatching {
        auth.signInWithEmailAndPassword(email, pass).await()
        auth.currentUser?.uid ?: throw IllegalStateException("UID missing")
    }

    /**
     * Get current role + branchId
     */
    suspend fun currentRoleBranch(): Pair<String, String?> {
        val uid = auth.currentUser?.uid ?: return "guest" to null
        val snap = db.collection("users").document(uid).get().await()
        return (snap.getString("role") ?: "user") to snap.getString("branchId")
    }
}