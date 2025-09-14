package com.pizzamania.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuCacheDao {
    @Query("SELECT * FROM menu_cache WHERE branchId=:branchId")
    suspend fun byBranch(branchId: String): List<MenuCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MenuCacheEntity>)

    @Query("DELETE FROM menu_cache WHERE branchId=:branchId")
    suspend fun clearBranch(branchId: String)

    // Additional methods if needed
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(menu: MenuCacheEntity)

    @Query("SELECT * FROM menu_cache WHERE id = :id")
    suspend fun getById(id: String): MenuCacheEntity?

    @Query("SELECT * FROM menu_cache")
    fun getAll(): Flow<List<MenuCacheEntity>>

    @Delete
    suspend fun delete(menu: MenuCacheEntity)
}