package com.example.pizzamaniaapp.ui.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.example.pizzamaniaapp.R
import com.pizzamania.data.repo.AuthRepo
import com.pizzamania.util.toast
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp

class AdminRegisterActivity : AppCompatActivity() {

    private val repo by lazy { AuthRepo(Firebase.auth, Firebase.firestore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminregister)

        val branchName = findViewById<AutoCompleteTextView>(R.id.etAdminBranch)
        val email = findViewById<TextInputEditText>(R.id.etAdminEmail)
        val pass = findViewById<TextInputEditText>(R.id.etAdminPassword)
        val confirm = findViewById<TextInputEditText>(R.id.etAdminConfirmPassword)
        val btnRegister = findViewById<MaterialButton>(R.id.btnAdminRegister)

        // 🔽 Attach adapter to dropdown (ensures values always show)
        val branches = resources.getStringArray(R.array.branches_array) // from strings.xml
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, branches)
        branchName.setAdapter(adapter)

        btnRegister.setOnClickListener {
            val b = branchName.text?.toString()?.trim().orEmpty()
            val e = email.text?.toString()?.trim().orEmpty()
            val p = pass.text?.toString().orEmpty()
            val c = confirm.text?.toString().orEmpty()

            if (b.isEmpty()) { toast("Please select a branch"); return@setOnClickListener }
            if (e.isEmpty()) { toast("Enter email"); return@setOnClickListener }
            if (p.isEmpty()) { toast("Enter password"); return@setOnClickListener }
            if (p != c) { toast("Passwords mismatch"); return@setOnClickListener }

            // Map branch name -> branchId (replace with your Firestore branch IDs if needed)
            val branchId = when (b) {
                "Colombo" -> "colombo_id"
                "Galle"   -> "galle_id"
                else -> null
            }
            if (branchId == null) {
                toast("Invalid branch selection")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                repo.registerAdmin(e, p, branchId).onSuccess { uid ->
                    // 🔽 Save admin profile into Firestore
                    val adminDoc = mapOf(
                        "email" to e,
                        "role" to "admin",
                        "branchName" to b,       // store readable branch name
                        "branchId" to branchId,  // store internal branchId
                        "createdAt" to Timestamp.now()
                    )
                    Firebase.firestore.collection("users").document(uid).set(adminDoc)
                        .addOnSuccessListener {
                            toast("Admin registered for $b branch")
                            finish()
                        }
                        .addOnFailureListener { ex ->
                            toast("Created but failed to save: ${ex.message}")
                            finish()
                        }
                }.onFailure {
                    toast(it.message ?: "Failed")
                }
            }
        }
    }
}