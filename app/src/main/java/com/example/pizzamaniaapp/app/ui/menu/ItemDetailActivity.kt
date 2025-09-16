package com.example.pizzamaniaapp.app.ui.menu

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.pizzamania.R

class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ make sure the file name matches: activity_item_detail
        setContentView(R.layout.activity_item_detail)

        val backBtn = findViewById<ImageButton>(R.id.btnBack)
        val ivPizza = findViewById<ImageView>(R.id.ivPizza)
        val tvName = findViewById<TextView>(R.id.tvPizzaName)
        val tvDesc = findViewById<TextView>(R.id.tvDescription)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val tvToppings = findViewById<TextView>(R.id.tvToppings)

        // Get the data passed from MenuListActivity
        val name = intent.getStringExtra("name")
        val desc = intent.getStringExtra("desc")
        val price = intent.getDoubleExtra("price", 0.0)
        val imgUrl = intent.getStringExtra("imgUrl")
        val toppings = intent.getStringArrayListExtra("toppings")

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

        backBtn.setOnClickListener { finish() }
    }
}