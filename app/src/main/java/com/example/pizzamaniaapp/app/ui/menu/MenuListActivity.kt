package com.example.pizzamaniaapp.app.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.pizzamania.R
import com.pizzamania.data.model.MenuItem
import com.example.pizzamaniaapp.data.repo.MenuRepo
import com.example.pizzamaniaapp.app.PizzaApp
import kotlinx.coroutines.launch

class MenuListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_menu)

        val recycler = findViewById<RecyclerView>(R.id.recyclerMenu)
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = MenuAdapter { item ->
            val parcel = MenuItemParcel(item.id, item.branchId, item.name, item.description, item.price, item.imageUrl, item.stock)
            startActivity(Intent(this, ProductDetailsActivity::class.java).apply { putExtra("item", parcel) })
        }
        recycler.adapter = adapter

        val menuRepo = MenuRepo(Firebase.firestore, Firebase.storage, PizzaApp.database.menuCacheDao())
        lifecycleScope.launch {
            // choose branchId — e.g., first branch or passed in intent
            val branchId = intent.getStringExtra("branchId") ?: run {
                // fallback: pick first branch from Firestore
                val b = Firebase.firestore.collection("branches").get().await()
                b.documents.firstOrNull()?.id ?: return@launch
            }
            val list = menuRepo.syncMenus(branchId)
            adapter.submit(list)
        }
    }
}