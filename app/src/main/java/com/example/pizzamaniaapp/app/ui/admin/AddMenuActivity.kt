package com.example.pizzamaniaapp.app.ui.admin

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pizzamania.R
import com.example.pizzamaniaapp.app.PizzaApp
import com.pizzamania.data.repo.BranchRepo
import com.example.pizzamaniaapp.data.repo.MenuRepo
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch

class AddMenuActivity : AppCompatActivity() {


    private val menuRepo by lazy {
        MenuRepo(
            Firebase.firestore,
            Firebase.storage,
            PizzaApp.instance.database.menuCacheDao()
        )
    }

    private var selectedBranchId: String? = null
    private var pickedImage: Uri? = null

    private val pick = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pickedImage = uri
        uri?.let { findViewById<ImageView>(R.id.ivAdminProfile).setImageURI(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // IMPORTANT: make sure this exists: res/layout/activity_add_menu.xml
        setContentView(R.layout.activity_anmin_add_menu)

        val spinner = findViewById<Spinner>(R.id.spinnerBranch)
        lifecycleScope.launch { bindBranches(spinner) }

        findViewById<ImageView>(R.id.ivAdminProfile).setOnClickListener { pick.launch("image/*") }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddMenuItem)
            .setOnClickListener {
                val name = findViewById<TextView>(R.id.textViewFoodName).text.toString().ifEmpty { "Pizza" }
                val desc = findViewById<TextView>(R.id.textViewDescription).text.toString()
                val priceText = findViewById<TextView>(R.id.textViewPrice).text.toString()
                val price = priceText.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 0.0

                val branchId = selectedBranchId ?: run {
                    toast("Select branch")
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    menuRepo.addOrUpdateMenu(
                        branchId = branchId,
                        itemId = null,
                        name = name,
                        desc = desc,
                        price = price,
                        stock = 25,
                        imageUri = pickedImage
                    ).onSuccess {
                        toast("Menu saved")
                    }.onFailure {
                        toast(it.message ?: "Failed")
                    }
                }
            }
    }

    private suspend fun bindBranches(spinner: Spinner) {
        val list = BranchRepo(Firebase.firestore).getBranches()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            list.map { it.name }
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, pos: Int, id: Long
            ) {
                selectedBranchId = list[pos].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}