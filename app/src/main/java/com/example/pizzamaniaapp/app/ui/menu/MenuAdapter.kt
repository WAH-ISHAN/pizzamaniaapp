package com.example.pizzamaniaapp.app.ui.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pizzamania.R
import com.pizzamania.data.model.MenuItem
import android.widget.ImageView
import com.bumptech.glide.Glide

class MenuAdapter(private val onClick: (MenuItem) -> Unit) : RecyclerView.Adapter<MenuAdapter.VH>() {
    private val data = mutableListOf<MenuItem>()
    fun submit(list: List<MenuItem>) { data.clear(); data.addAll(list); notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.row_cart_simple, parent, false))

    override fun getItemCount() = data.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = data[position]
        holder.name.text = item.name
        holder.price.text = "Rs. ${"%.2f".format(item.price)}"
        Glide.with(holder.image.context).load(item.imageUrl).into(holder.image)
        holder.itemView.setOnClickListener { onClick(item) }
    }

    class VH(v: View): RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvName)
        val price: TextView = v.findViewById(R.id.tvPrice)
        val image: ImageView = v.findViewById(R.id.ivThumb)
    }
}