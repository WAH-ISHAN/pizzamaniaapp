package com.example.pizzamaniaapp.app.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pizzamania.R

class RegisterActivity : AppCompatActivity() {

    private val auth get() = Firebase.auth
    private val db get() = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName = findViewById<TextInputEditText>(R.id.etName2)
        val etPassword = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val btnSave = findViewById<Button>(R.id.button)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnNext = findViewById<ImageButton>(R.id.btnNext)

        btnBack.setOnClickListener { finish() }
        btnNext.setOnClickListener { btnSave.performClick() }

        btnSave.setOnClickListener {
            val name = etFullName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()

            when {
                name.isEmpty() -> { etFullName.error = "Enter your name"; etFullName.requestFocus(); return@setOnClickListener }
                email.isEmpty() -> { etEmail.error = "Enter your email"; etEmail.requestFocus(); return@setOnClickListener }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { etEmail.error = "Enter a valid email"; etEmail.requestFocus(); return@setOnClickListener }
                password.length < 6 -> { etPassword.error = "Password must be at least 6 characters"; etPassword.requestFocus(); return@setOnClickListener }
            }

            btnSave.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener
                    val userDoc = mapOf(
                        "name" to name,
                        "email" to email,
                        "role" to "user",
                        "createdAt" to Timestamp.now()
                    )
                    db.collection("users").document(uid).set(userDoc)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Registered, but failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
                            finish()
                        }
                }
                .addOnFailureListener { e ->
                    btnSave.isEnabled = true
                    Toast.makeText(this, e.message ?: "Registration failed", Toast.LENGTH_LONG).show()
                }
        }
    }
}