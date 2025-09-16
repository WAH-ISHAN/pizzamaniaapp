package com.example.pizzamaniaapp.app.ui.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzamaniaapp.R  // 👈 fixed R import to your namespace
import com.example.pizzamaniaapp.ui.LoginActivity
import com.example.pizzamaniaapp.app.ui.home.HomeActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ActivityProfile : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var ivProfile: ImageView
    private lateinit var btnCapture: Button
    private lateinit var btnSave: Button
    private lateinit var btnRemoveAcc: Button
    private lateinit var etCurrentPass: EditText
    private lateinit var etNewPass: EditText
    private lateinit var etEmail: EditText

    private lateinit var mAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var imageUri: Uri? = null

    /** 📷 Gallery chooser */
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            imageUri = result.data!!.data
            ivProfile.setImageURI(imageUri)
        }
    }

    /** 📸 Camera chooser */
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val photo = result.data!!.extras?.get("data") as Bitmap
            ivProfile.setImageBitmap(photo)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // --- Firebase ---
        mAuth = FirebaseAuth.getInstance()
        user = mAuth.currentUser
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // --- Bind UI ---
        btnBack = findViewById(R.id.btnPBackarrow)
        ivProfile = findViewById(R.id.ivProfile)
        btnCapture = findViewById(R.id.btnCaptureP)
        btnSave = findViewById(R.id.btnSave)
        btnRemoveAcc = findViewById(R.id.btnRemoveAcc)
        etCurrentPass = findViewById(R.id.editTextTextPassword)
        etNewPass = findViewById(R.id.editTextTextPassword2)
        etEmail = findViewById(R.id.editTextTextEmailAddress)

        // 🔙 Back → Home
        btnBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // 📸 Capture Photo (Gallery/Camera)
        btnCapture.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)

            // Optionally camera:
            // val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            // cameraLauncher.launch(cameraIntent)
        }

        // 💾 Update Profile
        btnSave.setOnClickListener {
            updateProfile()
        }

        // ❌ Remove account
        btnRemoveAcc.setOnClickListener {
            user?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Account Deleted!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error deleting account!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /** 🔐 Update Email & Password with Re-authentication */
    private fun updateProfile() {
        val currentPass = etCurrentPass.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val newPassword = etNewPass.text.toString().trim()

        if (user != null && currentPass.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(user!!.email!!, currentPass)
            user!!.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                if (reAuthTask.isSuccessful) {

                    // Update email
                    if (newEmail.isNotEmpty()) {
                        user!!.updateEmail(newEmail).addOnSuccessListener {
                            db.collection("users").document(user!!.uid)
                                .update("email", newEmail)
                            Toast.makeText(this, "Email updated!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Update password
                    if (newPassword.isNotEmpty()) {
                        user!!.updatePassword(newPassword).addOnSuccessListener {
                            Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Upload profile image
                    uploadImageToFirebase()

                } else {
                    Toast.makeText(this, "Re-authentication failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Enter current password first!", Toast.LENGTH_SHORT).show()
        }
    }

    /** ☁️ Upload image to Firebase Storage */
    private fun uploadImageToFirebase() {
        imageUri?.let {
            val ref = storage.reference.child("profileImages/${user?.uid}.jpg")
            ref.putFile(it)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                        db.collection("users").document(user!!.uid)
                            .update("photoUrl", downloadUrl.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }
}