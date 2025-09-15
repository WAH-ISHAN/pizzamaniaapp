package com.example.pizzamaniaapp.app.ui.menu

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.pizzamania.R

class MenuDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail) // 🔹 use the detail XML layout file

        // 🔹 Find views
        val backBtn = findViewById<ImageButton>(R.id.btnBack)
        val ivPizza = findViewById<ImageView>(R.id.ivPizza)
        val tvName = findViewById<TextView>(R.id.tvPizzaName)
        val tvDesc = findViewById<TextView>(R.id.tvDescription)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)

        // 🔹 Get data from intent
        val name = intent.getStringExtra("name")
        val desc = intent.getStringExtra("desc")
        val price = intent.getDoubleExtra("price", 0.0)
        val imgUrl = intent.getStringExtra("imgUrl")

        // 🔹 Fill UI
        tvName.text = name
        tvDesc.text = desc
        tvPrice.text = "Rs. %.2f".format(price)

        Glide.with(this)
            .load(imgUrl)
            .placeholder(R.drawable.placeholder)
            .into(ivPizza)

        // 🔹 Back button (return to MenuListActivity)
        backBtn.setOnClickListener {
            finish()
        }
    }
}