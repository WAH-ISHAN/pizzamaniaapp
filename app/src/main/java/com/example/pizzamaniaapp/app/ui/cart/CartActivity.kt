package com.example.pizzamaniaapp.app.ui.cart

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pizzamania.R
import com.example.pizzamaniaapp.app.PizzaApp
import com.pizzamania.data.repo.CartRepo
import com.example.pizzamaniaapp.app.ui.order.PlaceOrderActivity
import com.pizzamania.util.toast
import android.content.Intent
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {
    private val cart by lazy { CartRepo(PizzaApp.database.cartDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_cart)



        val container = findViewById<LinearLayout>(R.id.layoutCartItems)
        lifecycleScope.launch {
            val items = cart.items()
            container.removeAllViews()
            items.forEach {
                val row = layoutInflater.inflate(R.layout.row_cart_simple, container, false)
                row.findViewById<TextView>(R.id.tvNameQty).text = " ${it.name} x ${it.qty}"
                row.findViewById<TextView>(R.id.tvPrice).text = "Rs. ${"%.2f".format(it.price * it.qty)}"
                container.addView(row)
            }
        }

        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            startActivity(Intent(this, PlaceOrderActivity::class.java))
        }
    }
}