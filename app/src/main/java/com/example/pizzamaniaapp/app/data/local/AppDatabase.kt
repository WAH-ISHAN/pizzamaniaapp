package com.example.pizzamaniaapp.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MenuCacheEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuCacheDao(): MenuCacheDao
}
