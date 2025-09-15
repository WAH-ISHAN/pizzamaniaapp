package com.example.pizzamaniaapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pizzamania.R
import com.pizzamania.ui.admin.AdminDashboardActivity
import com.example.pizzamaniaapp.app.ui.home.HomeActivity
import com.example.pizzamaniaapp.app.ui.auth.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private val auth get() = Firebase.auth
    private val db get() = Firebase.firestore

    private var selectedRole: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // role switch (optional UI)
        findViewById<MaterialButton>(R.id.btnAdmin).setOnClickListener { selectedRole = "admin" }
        findViewById<MaterialButton>(R.id.btnUser).setOnClickListener { selectedRole = "user" }

        // SIGN IN button
        findViewById<Button>(R.id.btnPrimary).setOnClickListener { view ->
            val email = findViewById<TextInputEditText>(R.id.etEmail).text?.toString()?.trim().orEmpty()
            val pass = findViewById<TextInputEditText>(R.id.etPassword).text?.toString().orEmpty()

            if (email.isEmpty()) { toast("Enter email"); return@setOnClickListener }
            if (pass.isEmpty()) { toast("Enter password"); return@setOnClickListener }

            view.isEnabled = false

            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { res ->
                    val uid = res.user?.uid
                    if (uid == null) {
                        view.isEnabled = true
                        toast("Login failed: no user")
                        return@addOnSuccessListener
                    }

                    // get user role from db
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            val role = doc.getString("role") ?: "user"

                            if (role == "admin") {
                                startActivity(Intent(this, AdminDashboardActivity::class.java))
                            } else {
                                startActivity(Intent(this, HomeActivity::class.java))
                            }
                            finish()
                        }
                        .addOnFailureListener { e ->
                            view.isEnabled = true
                            toast("Failed to get profile: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    view.isEnabled = true
                    toast(e.message ?: "Login failed")
                }
        }

        // Register link
        findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}