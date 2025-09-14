package com.pizzamania.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pizzamania.data.local.CartItemEntity
import com.pizzamania.data.local.MenuCacheEntity

@Database(
    entities = [CartItemEntity::class, MenuCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun menuCacheDao(): MenuCacheDao
}