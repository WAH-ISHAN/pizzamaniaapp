package com.example.pizzamaniaapp.app.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzamaniaapp.R
import com.pizzamania.data.model.MenuItem

class MenuAdapter(
    private val onClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuVH>() {

    private val items = mutableListOf<MenuItem>()

    fun submit(list: List<MenuItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_menu_item, parent, false)
        return MenuVH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MenuVH, position: Int) {
        val item = items[position]
        holder.bind(item, onClick)
    }

    class MenuVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumb: ImageView = itemView.findViewById(R.id.ivThumb)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvToppings: TextView = itemView.findViewById(R.id.tvToppings)

        fun bind(menu: MenuItem, onClick: (MenuItem) -> Unit) {
            tvName.text = menu.name
            tvDesc.text = menu.description
            tvPrice.text = "Rs. %.2f".format(menu.price)
            tvToppings.text =
                if (menu.toppings.isNotEmpty()) "Toppings: ${menu.toppings.joinToString(", ")}"
                else "No toppings"

            Glide.with(itemView.context)
                .load(menu.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(ivThumb)

            itemView.setOnClickListener { onClick(menu) }
        }
    }
}