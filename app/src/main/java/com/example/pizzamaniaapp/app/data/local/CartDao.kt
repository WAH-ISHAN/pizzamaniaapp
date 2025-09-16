package com.pizzamania.data.local

import androidx.room.*

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Query("SELECT * FROM cart_items")
    suspend fun all(): List<CartItemEntity>

    @Query("SELECT * FROM cart_items WHERE menuId = :menuId LIMIT 1")
    suspend fun findByMenuId(menuId: String): CartItemEntity?

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}