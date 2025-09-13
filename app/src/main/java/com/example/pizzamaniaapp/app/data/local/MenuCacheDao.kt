package com.example.pizzamaniaapp.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(menu: MenuCacheEntity)

    @Query("SELECT * FROM menu_cache WHERE id = :id")
    suspend fun getById(id: Long): MenuCacheEntity?

    @Query("SELECT * FROM menu_cache")
    fun getAll(): Flow<List<MenuCacheEntity>>

    @Delete
    suspend fun delete(menu: MenuCacheEntity)
}
