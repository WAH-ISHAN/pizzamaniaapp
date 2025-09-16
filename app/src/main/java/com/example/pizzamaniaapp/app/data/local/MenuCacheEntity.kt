package com.pizzamania.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a cached menu item in the local Room database.
 * Mirrors the Firestore document: branches/{branchId}/menus/{menuId}
 */
@Entity(tableName = "menu_cache")
data class MenuCacheEntity(
    @PrimaryKey val id: String,
    val branchId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Long,
    val toppings: List<String> = emptyList() // ✅ OK with TypeConverter
)