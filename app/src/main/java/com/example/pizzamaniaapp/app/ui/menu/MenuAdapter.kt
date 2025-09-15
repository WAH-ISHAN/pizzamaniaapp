package com.example.pizzamaniaapp.app.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pizzamania.R
import com.pizzamania.data.model.MenuItem // ✅ assume your data model has (id, name, desc, price, imageUrl)

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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_menu_list, parent, false)
        return MenuVH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MenuVH, position: Int) {
        val item = items[position]
        holder.bind(item, onClick)
    }

    class MenuVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumb = itemView.findViewById<ImageView>(R.id.ivThumb)
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvDesc = itemView.findViewById<TextView>(R.id.tvDesc)
        private val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)

        fun bind(menu: MenuItem, onClick: (MenuItem) -> Unit) {
            tvName.text = menu.name
            tvDesc.text = menu.description
            tvPrice.text = "Rs. ${menu.price}"
            Glide.with(itemView.context)
                .load(menu.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(ivThumb)

            itemView.setOnClickListener { onClick(menu) }
        }
    }
}