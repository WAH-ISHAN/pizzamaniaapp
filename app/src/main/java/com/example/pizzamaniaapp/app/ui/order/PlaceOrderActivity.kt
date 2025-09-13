package com.example.pizzamaniaapp.app.ui.order

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pizzamania.R
import com.example.pizzamaniaapp.app.PizzaApp
import com.pizzamania.data.repo.BranchRepo
import com.pizzamania.data.repo.CartRepo
import com.example.pizzamaniaapp.app.data.repo.OrderRepo
import com.pizzamania.util.toast
import kotlinx.coroutines.launch

class PlaceOrderActivity : AppCompatActivity() {
    private val cart by lazy { CartRepo(PizzaApp.database.cartDao()) }
    private val orderRepo by lazy { OrderRepo(Firebase.firestore, Firebase.auth, BranchRepo(Firebase.firestore)) }
    private var currentLat: Double? = null
    private var currentLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        getLastLocation()

        findViewById<Button>(R.id.btnPlaceOrder).setOnClickListener {
            val address = findViewById<TextInputEditText>(R.id.etAddress).text?.toString()?.trim().orEmpty()
            val phone = findViewById<TextInputEditText>(R.id.etPhone).text?.toString()?.trim().orEmpty()
            val payment = "COD"
            lifecycleScope.launch {
                val items = cart.items()
                if (items.isEmpty()) { toast("Cart empty"); return@launch }
                orderRepo.placeOrder(address, phone, payment, currentLat, currentLng, items)
                    .onSuccess {
                        cart.clear()
                        toast("Order placed! #$it")
                    }
                    .onFailure { toast(it.message ?: "Order failed") }
            }
        }
    }

    private fun getLastLocation() {
        val fused = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fused.lastLocation.addOnSuccessListener { loc -> if (loc != null) { currentLat = loc.latitude; currentLng = loc.longitude } }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }
}