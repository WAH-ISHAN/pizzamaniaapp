package com.example.pizzamaniaapp.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.pizzamaniaapp.app.data.entities.MenuItem;
import java.util.List;

@Dao
public interface MenuItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<MenuItem> items);

    @Query("SELECT * FROM menu_items ORDER BY name ASC")
    LiveData<List<MenuItem>> getAll();

    @Query("DELETE FROM menu_items")
    void clear();
}