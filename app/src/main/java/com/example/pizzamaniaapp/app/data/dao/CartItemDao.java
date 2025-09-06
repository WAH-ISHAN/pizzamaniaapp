package com.example.pizzamaniaapp.app.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.pizzamaniaapp.app.data.entities.CartItem;

import java.util.List;

@Dao
public interface CartItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(CartItem item);

    @Update
    int update(CartItem item);

    @Delete
    int delete(CartItem item);

    @Query("SELECT * FROM cart_items")
    LiveData<List<CartItem>> observeCart();

    @Query("DELETE FROM cart_items")
    void clear();

    @Query("SELECT SUM(price * quantity) FROM cart_items")
    LiveData<Double> observeTotal();
}