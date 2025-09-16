package com.pizzamania.app

import android.app.Application
import androidx.room.Room
import com.pizzamania.data.local.PizzaDatabase

/**
 * Global Application class - initializes Room DB once.
 */
class PizzaApp : Application() {

    companion object {
        lateinit var instance: PizzaApp
            private set

        lateinit var database: PizzaDatabase   // ✅ use unified DB class
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            PizzaDatabase::class.java,
            "pizza-db"
        )
            .fallbackToDestructiveMigration() // Dev-only (drops DB on schema change)
            .build()
    }
}