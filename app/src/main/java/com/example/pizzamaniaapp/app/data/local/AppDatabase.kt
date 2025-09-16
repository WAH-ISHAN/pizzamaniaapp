package com.pizzamania.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for PizzaMania.
 *
 * Stores:
 *   - Cart items (CartItemEntity)
 *   - Menu cache entries (MenuCacheEntity)
 *
 * @TypeConverters is used to serialize complex types like List<String> (toppings).
 *
 * ⚠️ IMPORTANT: Increment the version number every time you change entity schemas.
 */
@Database(
    entities = [
        CartItemEntity::class,   // ✅ your cart table entity
        MenuCacheEntity::class   // ✅ your menu cache table
    ],
    version = 4,                 // bumped since toppings was added
    exportSchema = false
)
@TypeConverters(Converters::class)   // used to persist List<String> → CSV
abstract class PizzaDatabase : RoomDatabase() {

    /** DAO for managing Cart items */
    abstract fun cartDao(): CartDao

    /** DAO for managing Menu cache */
    abstract fun menuCacheDao(): MenuCacheDao
}