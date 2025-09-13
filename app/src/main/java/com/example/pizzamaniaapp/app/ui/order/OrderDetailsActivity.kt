package com.example.pizzamaniaapp.app.ui.order

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pizzamania.R

class OrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        val orderId = intent.getStringExtra("orderId") ?: return
        Firebase.firestore.collection("orders").document(orderId).addSnapshotListener { snap, _ ->
            if (snap != null && snap.exists()) {
                findViewById<TextView>(R.id.tvId).text = "Order ID: $orderId"
                findViewById<TextView>(R.id.tvStatus).text = "Status: " + (snap.getString("status") ?: "")
                findViewById<TextView>(R.id.tvBranch).text = "Branch: " + (snap.getString("branchId") ?: "")
                findViewById<TextView>(R.id.tvAddress).text = "Address: " + (snap.getString("address") ?: "")
                findViewById<TextView>(R.id.tvPayment).text = "Payment: " + (snap.getString("payment") ?: "")
                findViewById<TextView>(R.id.tvTotal).text = "Total: Rs. " + (snap.getDouble("total") ?: 0.0)
            }
        }
    }
}