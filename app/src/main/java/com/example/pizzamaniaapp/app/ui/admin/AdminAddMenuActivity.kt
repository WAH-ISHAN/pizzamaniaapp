package com.example.pizzamaniaapp.ui.admin

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.example.pizzamaniaapp.R
import com.example.pizzamaniaapp.app.PizzaApp
import com.pizzamania.data.repo.MenuRepo
import kotlinx.coroutines.launch
import java.io.File

class AdminAddMenuActivity : AppCompatActivity() {

    private val menuRepo by lazy {
        MenuRepo(
            Firebase.firestore,
            Firebase.storage,
            PizzaApp.database.menuCacheDao()
        )
    }

    // UI references
    private lateinit var ivFood: ImageView
    private lateinit var etBranch: AutoCompleteTextView
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var btnChooseImage: Button
    private lateinit var btnAddMenuItem: Button
    private lateinit var btnRemoveMenuItem: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnAddTopping: Button
    private lateinit var toppingsContainer: ChipGroup

    // State
    private val toppingsList = mutableListOf<String>()
    private var selectedImageUri: Uri? = null
    private var cameraImageUri: Uri? = null
    private var branchIdMap: Map<String, String> = emptyMap()

    // IDs passed if editing
    private var editingBranchId: String? = null
    private var editingItemId: String? = null

    // Gallery picker
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data?.data != null) {
            selectedImageUri = result.data?.data
            ivFood.setImageURI(selectedImageUri)
        }
    }

    // Camera capture launcher
    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val uri: Uri? = cameraImageUri   // safe local copy
        if (success && uri != null) {
            selectedImageUri = uri
            ivFood.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_menu)

        // If editing existing
        editingBranchId = intent.getStringExtra("branchId")
        editingItemId = intent.getStringExtra("itemId")

        // Bind UI
        ivFood = findViewById(R.id.ivAdminFood)
        etBranch = findViewById(R.id.etAdminBranch)
        etName = findViewById(R.id.etFoodName)
        etDescription = findViewById(R.id.etDescription)
        etPrice = findViewById(R.id.etPrice)
        btnChooseImage = findViewById(R.id.btnChooseImage)
        btnAddMenuItem = findViewById(R.id.btnAddMenuItem)
        btnRemoveMenuItem = findViewById(R.id.btnRemoveMenuItem) // This will act like Back
        btnBack = findViewById(R.id.btnBackDashboard)
        btnAddTopping = findViewById(R.id.btnAddTopping)
        toppingsContainer = findViewById(R.id.toppingsContainer)

        // Top back arrow: go back to dashboard
        btnBack.setOnClickListener {
            navigateBackToDashboard()
        }

        // "Remove" button (renamed to "Back"): go back to dashboard
        btnRemoveMenuItem.setOnClickListener {
            navigateBackToDashboard()
        }

        loadBranches()

        btnChooseImage.setOnClickListener { showImageSourceDialog() }
        btnAddTopping.setOnClickListener { promptAddTopping() }
        btnAddMenuItem.setOnClickListener { saveMenuItem() }
    }

    /** Navigate back to Admin Dashboard */
    private fun navigateBackToDashboard() {
        val intent = Intent(this, AdminDashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    /** Show Camera/Gallery choice */
    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickGalleryLauncher.launch(intent)
    }

    private fun openCamera() {
        val photoFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "menu_item_${System.currentTimeMillis()}.jpg"
        )
        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.provider",
            photoFile
        )
        cameraImageUri = uri
        takePhotoLauncher.launch(uri)
    }

    /** load branches from string-array */
    private fun loadBranches() {
        val branches = resources.getStringArray(R.array.branches_array).toList()
        branchIdMap = branches.associateWith { it }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            branches
        )
        etBranch.setAdapter(adapter)
        etBranch.setOnClickListener { etBranch.showDropDown() }
    }

    /** toppings selection */
    private fun promptAddTopping() {
        val allToppings = resources.getStringArray(R.array.toppings_array)
        val checked = BooleanArray(allToppings.size)

        AlertDialog.Builder(this)
            .setTitle("Select Toppings")
            .setMultiChoiceItems(allToppings, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton("Add") { _, _ ->
                allToppings.forEachIndexed { idx, label ->
                    if (checked[idx] && !toppingsList.contains(label)) {
                        toppingsList.add(label)
                        addToppingChip(label)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addToppingChip(label: String) {
        val chip = Chip(this).apply {
            text = label
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                toppingsContainer.removeView(this)
                toppingsList.remove(label)
            }
        }
        toppingsContainer.addView(chip)
    }

    /** Save or update menu item */
    private fun saveMenuItem() {
        val branchName = etBranch.text.toString().trim()
        val branchId = branchIdMap[branchName]
        val name = etName.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val priceText = etPrice.text.toString().trim()

        if (branchId.isNullOrEmpty() || name.isEmpty() || description.isEmpty() ||
            priceText.isEmpty() || selectedImageUri == null
        ) {
            Toast.makeText(this, "Please fill all fields and choose an image", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null) {
            Toast.makeText(this, "Enter valid price", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                menuRepo.addOrUpdateMenu(
                    branchId = branchId,
                    itemId = editingItemId,
                    name = name,
                    desc = description,
                    price = price,
                    stock = 1L,
                    imageUri = selectedImageUri,
                    toppings = toppingsList
                )
                Toast.makeText(this@AdminAddMenuActivity, "Menu item saved successfully!", Toast.LENGTH_SHORT).show()
                navigateBackToDashboard()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@AdminAddMenuActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}