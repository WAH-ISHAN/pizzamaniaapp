package com.example.pizzamaniaapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.pizzamaniaapp.R
import com.example.pizzamaniaapp.app.ui.order.AdminOrdersActivity
import com.example.pizzamaniaapp.app.ui.menu.MenuListActivity

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        findViewById<Button>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, AdminOrdersActivity::class.java))
        }
        findViewById<Button>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, MenuListActivity::class.java))
        }
        findViewById<Button>(R.id.btnSeed).setOnClickListener {
            startActivity(Intent(this@AdminDashboardActivity, AdminAddMenuActivity::class.java))
        }
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            Firebase.auth.signOut()
            finish()
        }
    }
}