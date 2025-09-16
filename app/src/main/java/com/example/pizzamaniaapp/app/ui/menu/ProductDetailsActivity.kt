package com.example.pizzamaniaapp.app.ui.menu

import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pizzamaniaapp.R
import com.example.pizzamaniaapp.app.PizzaApp
import com.pizzamania.data.model.MenuItem
import com.pizzamania.data.repo.CartRepo
import com.pizzamania.util.toast
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItemParcel(
    val id: String,
    val branchId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Long,
    val toppings: List<String> = emptyList()
) : Parcelable

class ProductDetailsActivity : AppCompatActivity() {
    private val cart by lazy { CartRepo(PizzaApp.database.cartDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.row_item_detail)

        val p = intent.getParcelableExtra<MenuItemParcel>("item") ?: return
        val item = MenuItem(
            id = p.id,
            branchId = p.branchId,
            name = p.name,
            description = p.description,
            price = p.price,
            imageUrl = p.imageUrl,
            stock = p.stock,
            toppings = p.toppings   // ✅ works after fixing MenuItem model
        )

        val qtyTv = findViewById<TextView>(R.id.tvQty)
        var qty = 1
        fun update() { qtyTv.text = qty.toString().padStart(2, '0') }

        findViewById<Button>(R.id.btnPlus).setOnClickListener { qty++; update() }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { if (qty > 1) { qty--; update() } }
        findViewById<Button>(R.id.btnAddToCart).setOnClickListener {
            lifecycleScope.launch {
                cart.add(item, qty)
                toast("Added to cart with toppings: ${item.toppings.joinToString(", ")}")
            }
        }

        update()
    }
}