package com.example.pizzamaniaapp.app.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_cache")
data class MenuCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Double,
    val description: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
