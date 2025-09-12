package com.example.pizzamaniaapp.app

import android.app.Application
import androidx.room.Room
import com.pizzamania.data.local.AppDb

class PizzaApp : Application() {
    lateinit var db: AppDb
        private set

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(this, AppDb::class.java, "pizza.db").build()
        instance = this
    }
    companion object {
        lateinit var instance: PizzaApp
            private set
        val database get() = instance.db
    }
}
