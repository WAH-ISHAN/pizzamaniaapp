package com.example.pizzamaniaapp.app

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.pizzamania.data.local.PizzaDatabase

/**
 * Global Application class - initializes Room DB + Firebase once.
 */
class PizzaApp : Application() {

    companion object {
        lateinit var instance: PizzaApp
            private set

        lateinit var database: PizzaDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // ✅ Initialize Firebase
        FirebaseApp.initializeApp(this)

        // ✅ Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            PizzaDatabase::class.java,
            "pizza-db"
        )
            .fallbackToDestructiveMigration() // Dev-only (drops DB on schema change)
            .build()
    }
}