package com.pizzamania.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val menuId: String,
    val name: String,
    val price: Double,
    val imageUrl: String?,
    val qty: Int
)

@Entity(tableName = "menu_cache")
data class MenuCacheEntity(
    @PrimaryKey val id: String,
    val branchId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Long
)