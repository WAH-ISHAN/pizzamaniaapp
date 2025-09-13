package com.example.pizzamaniaapp.ui.admin

import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.pizzamania.R
import com.pizzamania.data.repo.AuthRepo
import com.pizzamania.util.toast
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

class AdminRegisterActivity : AppCompatActivity() {
    private val repo by lazy { AuthRepo(Firebase.auth, Firebase.firestore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminregister)



        val branchName = findViewById<AutoCompleteTextView>(R.id.etAdminBranch)
        val email = findViewById<TextInputEditText>(R.id.etAdminEmail)
        val pass = findViewById<TextInputEditText>(R.id.etAdminPassword)
        val confirm = findViewById<TextInputEditText>(R.id.etAdminConfirmPassword)

        findViewById<MaterialButton>(R.id.btnAdminRegister).setOnClickListener {
            val b = branchName.text?.toString()?.trim().orEmpty()
            val e = email.text?.toString()?.trim().orEmpty()
            val p = pass.text?.toString().orEmpty()
            val c = confirm.text?.toString().orEmpty()
            if (p != c) { toast("Passwords mismatch"); return@setOnClickListener }
            lifecycleScope.launch {
                val branchId = getBranchIdByName(b)
                if (branchId == null) { toast("Invalid branch"); return@launch }
                repo.registerAdmin(e, p, branchId).onSuccess {
                    toast("Admin registered")
                    finish()
                }.onFailure { toast(it.message ?: "Failed") }
            }
        }
    }

    private suspend fun getBranchIdByName(name: String): String? {
        val q = Firebase.firestore.collection("branches").whereEqualTo("name", name).get().await()
        return q.documents.firstOrNull()?.id
    }
}