package com.example.pizzamaniaapp.app.ui.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pizzamaniaapp.R
import com.example.pizzamaniaapp.app.ui.cart.CartActivity

class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.row_item_detail) // ✅ your item detail layout

        val backBtn = findViewById<ImageButton>(R.id.btnBack)
        val ivPizza = findViewById<ImageView>(R.id.ivPizza)
        val tvName = findViewById<TextView>(R.id.tvPizzaName)
        val tvDesc = findViewById<TextView>(R.id.tvDescription)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val tvToppings = findViewById<TextView>(R.id.tvToppings)
        val btnAddToCart = findViewById<Button>(R.id.btnAddToCart)

        // Get the data passed from MenuListActivity
        val name = intent.getStringExtra("name")
        val desc = intent.getStringExtra("desc")
        val price = intent.getDoubleExtra("price", 0.0)
        val imgUrl = intent.getStringExtra("imgUrl")
        val toppings = intent.getStringArrayListExtra("toppings_array") // ✅ fixed

        // Bind values
        tvName.text = name
        tvDesc.text = desc
        tvPrice.text = "Rs. %.2f".format(price)
        tvToppings.text =
            if (!toppings.isNullOrEmpty()) "Toppings: ${toppings.joinToString(", ")}"
            else "No toppings"

        Glide.with(this)
            .load(imgUrl)
            .placeholder(R.drawable.placeholder)
            .into(ivPizza)

        // Back button → finish this activity
        backBtn.setOnClickListener { finish() }

        // ✅ Add To Cart → open CartActivity with pizza data
        btnAddToCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java).apply {
                putExtra("name", name)
                putExtra("desc", desc)
                putExtra("price", price)
                putExtra("imgUrl", imgUrl)
                putStringArrayListExtra("toppings", toppings)
            }
            startActivity(intent)
        }
    }
}