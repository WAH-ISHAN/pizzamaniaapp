package com.example.pizzamaniaapp.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzamaniaapp.R
import com.example.pizzamaniaapp.ui.LoginActivity
import com.example.pizzamaniaapp.app.ui.menu.MenuListActivity
import com.example.pizzamaniaapp.app.ui.auth.ActivityProfile
import com.example.pizzamaniaapp.app.ui.map.BranchMapActivity
import com.example.pizzamaniaapp.app.ui.order.PlaceOrderActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private val auth get() = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<Button>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(this, BranchMapActivity::class.java))
        }

        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ActivityProfile::class.java))
        }

        findViewById<Button>(R.id.btnSeed).setOnClickListener {
            startActivity(Intent(this, MenuListActivity::class.java))
        }

        // ✅ FIXED: Order button goes directly to PlaceOrderActivity
        findViewById<Button>(R.id.btnSeed2).setOnClickListener {
            startActivity(Intent(this, PlaceOrderActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}