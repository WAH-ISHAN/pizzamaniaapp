package com.pizzamania.data.local

import androidx.room.*

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    suspend fun all(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Update
    suspend fun update(item: CartItemEntity)

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}