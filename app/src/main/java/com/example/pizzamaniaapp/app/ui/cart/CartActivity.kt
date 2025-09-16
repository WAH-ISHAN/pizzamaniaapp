package com.example.pizzamaniaapp.app.ui.cart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.pizzamaniaapp.R
import com.example.pizzamaniaapp.app.PizzaApp
import com.example.pizzamaniaapp.app.ui.order.PlaceOrderActivity
import com.pizzamania.data.repo.CartRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : AppCompatActivity() {
    private val cart by lazy { CartRepo(PizzaApp.database.cartDao()) }

    private lateinit var container: LinearLayout
    private lateinit var totalText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_cart) // ✅ main Cart screen

        container = findViewById(R.id.layoutCartItems)
        totalText = findViewById(R.id.tvTotal)

        loadCartUi()

        // Back Button → go back
        findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener { finish() }

        // Checkout Button → PlaceOrderActivity
        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            startActivity(Intent(this, PlaceOrderActivity::class.java))
        }
    }

    /** Refresh cart contents and rebuild UI */
    private fun loadCartUi() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) { cart.items() }
            container.removeAllViews()

            if (items.isEmpty()) {
                val empty = TextView(this@CartActivity).apply {
                    text = "Your cart is empty"
                    textSize = 16f
                    setPadding(20, 40, 20, 40)
                }
                container.addView(empty)
                totalText.text = "Total: Rs. 0.00"
            } else {
                var total = 0.0
                items.forEach { item ->
                    total += item.price * item.qty

                    // ✅ Inflate correct row layout file
                    val row = layoutInflater.inflate(R.layout.row_menu_item, container, false)

                    val ivThumb = row.findViewById<ImageView>(R.id.ivThumb)
                    val tvName = row.findViewById<TextView>(R.id.tvName)
                    val tvDesc = row.findViewById<TextView>(R.id.tvDesc)
                    val tvPrice = row.findViewById<TextView>(R.id.tvPrice)

                    tvName.text = item.name
                    tvDesc.text = "Qty: ${item.qty}" // repurposed 'desc' field for qty
                    tvPrice.text = "Rs. ${"%.2f".format(item.price * item.qty)}"

                    // Load image from entity (if null, use placeholder)
                    Glide.with(this@CartActivity)
                        .load(item.imageUrl ?: R.drawable.placeholder)
                        .placeholder(R.drawable.placeholder)
                        .into(ivThumb)

                    container.addView(row)
                }
                totalText.text = "Total: Rs. ${"%.2f".format(total)}"
            }
        }
    }
}