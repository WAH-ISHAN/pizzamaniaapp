package com.example.pizzamaniaapp.app

import android.app.Application
import androidx.room.Room
import com.example.pizzamaniaapp.app.data.local.AppDatabase

class PizzaApp : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "pizza-db"
        )
            // .fallbackToDestructiveMigration() // optional, careful with production data
            .build()
    }

    companion object {
        lateinit var instance: PizzaApp
            private set
    }
}
