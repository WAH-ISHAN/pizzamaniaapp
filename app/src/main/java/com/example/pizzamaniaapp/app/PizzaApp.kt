package com.pizzamania.app

import android.app.Application
import androidx.room.Room
import com.pizzamania.data.local.AppDb

class PizzaApp : Application() {

    companion object {
        // Application instance if needed elsewhere
        lateinit var instance: PizzaApp
            private set

        // Room database reference
        lateinit var database: AppDb
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Room database once when app starts
        database = Room.databaseBuilder(
            applicationContext,
            AppDb::class.java,
            "pizza-db"
        )
            .fallbackToDestructiveMigration() // for dev/testing; removes db if schema mismatch
            .build()
    }
}