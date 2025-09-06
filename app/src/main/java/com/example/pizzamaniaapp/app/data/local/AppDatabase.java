package com.example.pizzamaniaapp.app.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.pizzamaniaapp.app.data.dao.CartItemDao;
import com.example.pizzamaniaapp.app.data.dao.MenuItemDao;
import com.example.pizzamaniaapp.app.data.entities.CartItem;
import com.example.pizzamaniaapp.app.data.entities.MenuItem;

@Database(entities = {MenuItem.class, CartItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract MenuItemDao menuItemDao();
    public abstract CartItemDao cartItemDao();

    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "pizza_mania.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}