package com.example.pizzamaniaapp.app.ui.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.pizzamania.R
import com.pizzamania.app.PizzaApp
import com.pizzamania.data.repo.MenuRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_list)

        // 🔹 Top bar with back arrow
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { finish() }

        // 🔹 RecyclerView setup
        val recycler = findViewById<RecyclerView>(R.id.menuContainer)
        recycler.layoutManager = LinearLayoutManager(this)

        val adapter = MenuAdapter { item ->
            // 🔹 On item click → Go to detail page
            val i = Intent(this, MenuDetailActivity::class.java).apply {
                putExtra("name", item.name)
                putExtra("desc", item.description)
                putExtra("price", item.price)
                putExtra("imgUrl", item.imageUrl)
            }
            startActivity(i)
        }
        recycler.adapter = adapter

        // 🔹 Repository setup
        val menuRepo = MenuRepo(
            Firebase.firestore,
            FirebaseStorage.getInstance(),
            PizzaApp.database.menuCacheDao()
        )

        // 🔹 Load data from repo
        lifecycleScope.launch {
            try {
                val branchId = intent.getStringExtra("branchId") ?: run {
                    val b = Firebase.firestore.collection("branches").get().await()
                    b.documents.firstOrNull()?.id ?: return@launch
                }
                val list = menuRepo.syncMenus(branchId)
                adapter.submit(list)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MenuListActivity,
                    "Error loading menu",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}