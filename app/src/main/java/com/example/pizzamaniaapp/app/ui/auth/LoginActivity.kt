package com.example.pizzamaniaapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pizzamania.R
import com.pizzamania.data.repo.AuthRepo
import com.pizzamania.ui.admin.AdminDashboardActivity
import com.example.pizzamaniaapp.app.ui.home.HomeActivity
import com.pizzamania.util.toast
import com.google.android.material.button.MaterialButton
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.pizzamaniaapp.app.ui.auth.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val repo by lazy { AuthRepo(Firebase.auth, Firebase.firestore) }
    private var selectedRole: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // set your file name



        findViewById<MaterialButton>(R.id.btnAdmin).setOnClickListener { selectedRole = "admin" }
        findViewById<MaterialButton>(R.id.btnUser).setOnClickListener { selectedRole = "user" }

        findViewById<Button>(R.id.btnPrimary).setOnClickListener {
            val email = findViewById<TextInputEditText>(R.id.etEmail).text?.toString()?.trim().orEmpty()
            val pass = findViewById<TextInputEditText>(R.id.etPassword).text?.toString().orEmpty()
            lifecycleScope.launch {
                repo.signIn(email, pass).onSuccess {
                    val (role, _) = repo.currentRoleBranch()
                    if (role == "admin") startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                    else startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }.onFailure { toast(it.message ?: "Login failed") }
            }
        }

        findViewById<TextView>(R.id.tvRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}