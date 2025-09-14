package com.pizzamania.app

import android.app.Application
import androidx.room.Room
import com.pizzamania.data.local.AppDb

class PizzaApp : Application() {

    companion object {
        lateinit var instance: PizzaApp
            private set

        lateinit var database: AppDb
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = Room.databaseBuilder(
            applicationContext,
            AppDb::class.java,
            "pizza-db"
        )
            .fallbackToDestructiveMigration() // Enable for development
            .build()
    }
}