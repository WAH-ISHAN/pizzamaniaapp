package com.example.pizzamaniaapp.app.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.FirebaseStorage
import com.pizzamania.R
import com.pizzamania.app.PizzaApp
import com.pizzamania.data.repo.MenuRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_menu) // this file must contain RecyclerView with id 'recycler'



        val recycler = findViewById<RecyclerView>(R.id.menuContainer)
        recycler.layoutManager = LinearLayoutManager(this)

        val adapter = MenuAdapter { item ->
            val parcel = MenuItemParcel(
                item.id,
                item.branchId,
                item.name,
                item.description,
                item.price,
                item.imageUrl,
                item.stock
            )
            startActivity(
                Intent(this, ProductDetailsActivity::class.java).apply {
                    putExtra("item", parcel)
                }
            )
        }
        recycler.adapter = adapter

        val menuRepo = MenuRepo(
            Firebase.firestore,
            FirebaseStorage.getInstance(),
            PizzaApp.database.menuCacheDao()
        )

        lifecycleScope.launch {
            val branchId = intent.getStringExtra("branchId") ?: run {
                val b = Firebase.firestore.collection("branches").get().await()
                b.documents.firstOrNull()?.id ?: return@launch
            }
            val list = menuRepo.syncMenus(branchId)
            adapter.submit(list) // use your adapter's submit() method
        }
    }
}