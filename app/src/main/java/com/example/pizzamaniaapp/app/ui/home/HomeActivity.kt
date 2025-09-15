package com.example.pizzamaniaapp.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pizzamania.R
import com.example.pizzamaniaapp.ui.LoginActivity
import com.example.pizzamaniaapp.app.ui.menu.MenuListActivity

class HomeActivity : AppCompatActivity() {

    private val auth get() = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔹 Branches map
        findViewById<Button>(R.id.btnMap).setOnClickListener {
            Toast.makeText(this, "Opening branches map...", Toast.LENGTH_SHORT).show()
            // Example: startActivity(Intent(this, BranchMapActivity::class.java))
        }

        // 🔹 Profile
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            Toast.makeText(this, "Profile opening...", Toast.LENGTH_SHORT).show()
            // Example: startActivity(Intent(this, ProfileActivity::class.java))
        }

        // 🔹 Pizza Menu
        findViewById<Button>(R.id.btnSeed).setOnClickListener {
            val intent = Intent(this, MenuListActivity::class.java)
            startActivity(intent)
        }

        // 🔹 Order
        findViewById<Button>(R.id.btnSeed2).setOnClickListener {
            Toast.makeText(this, "Order page opening...", Toast.LENGTH_SHORT).show()
            // Example: startActivity(Intent(this, OrderActivity::class.java))
        }

        // 🔹 Logout
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            // Clear back stack so user can't press back to come here
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}