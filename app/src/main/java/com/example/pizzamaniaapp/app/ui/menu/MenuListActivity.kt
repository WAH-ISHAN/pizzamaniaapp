package com.example.pizzamaniaapp.app.ui.menu

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

        // 🔹 Top bar (back arrow)
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        toolbar.setNavigationOnClickListener { finish() }

        // 🔹 RecyclerView setup
        val recycler = findViewById<RecyclerView>(R.id.menuContainer)
        recycler.layoutManager = LinearLayoutManager(this)

        val adapter = MenuAdapter { item ->
            Toast.makeText(this, "Clicked ${item.name}", Toast.LENGTH_SHORT).show()
        }
        recycler.adapter = adapter

        // 🔹 Repository setup
        val menuRepo = MenuRepo(
            Firebase.firestore,
            FirebaseStorage.getInstance(),
            PizzaApp.database.menuCacheDao()
        )

        // 🔹 Load data
        lifecycleScope.launch {
            try {
                val branchId = intent.getStringExtra("branchId") ?: run {
                    // fetch any default branch if none passed
                    val b = Firebase.firestore.collection("branches").get().await()
                    b.documents.firstOrNull()?.id ?: return@launch
                }
                val list = menuRepo.syncMenus(branchId)
                adapter.submit(list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}