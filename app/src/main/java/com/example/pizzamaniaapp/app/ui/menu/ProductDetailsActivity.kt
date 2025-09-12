package com.example.pizzamaniaapp.app.ui.menu

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.pizzamania.R
import com.example.pizzamaniaapp.app.PizzaApp
import com.pizzamania.data.model.MenuItem
import com.pizzamania.data.repo.CartRepo
import com.pizzamania.util.toast
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import kotlinx.coroutines.launch

@Parcelize
data class MenuItemParcel(
    val id: String, val branchId: String, val name: String,
    val description: String, val price: Double, val imageUrl: String?, val stock: Long
): Parcelable

class ProductDetailsActivity : AppCompatActivity() {
    private val cart by lazy { CartRepo(PizzaApp.database.cartDao()) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val p = intent.getParcelableExtra<MenuItemParcel>("item") ?: return
        val item = MenuItem(p.id, p.branchId, p.name, p.description, p.price, p.imageUrl, p.stock)

        val qtyTv = findViewById<TextView>(R.id.tvQty)
        var qty = 1
        fun update() { qtyTv.text = qty.toString().padStart(2, '0') }
        findViewById<Button>(R.id.btnPlus).setOnClickListener { qty++; update() }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { if (qty > 1) { qty--; update() } }
        findViewById<Button>(R.id.btnAddToCart).setOnClickListener {
            lifecycleScope.launch { cart.add(item, qty); toast("Added to cart") }
        }
        update()
    }
}