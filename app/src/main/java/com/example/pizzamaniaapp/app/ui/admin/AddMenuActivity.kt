package com.pizzamania.ui.admin

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pizzamania.R
import com.pizzamania.app.PizzaApp
import com.pizzamania.data.repo.BranchRepo
import com.pizzamania.data.repo.MenuRepo
import kotlinx.coroutines.launch

class AddMenuActivity : AppCompatActivity() {

    private val menuRepo by lazy {
        MenuRepo(
            Firebase.firestore,
            Firebase.storage,
            PizzaApp.database.menuCacheDao()
        )
    }

    // ... rest of the implementation
}