package com.pizzamania.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for caching menu items locally for offline use.
 */
@Dao
interface MenuCacheDao {

    // 🔹 Insert or update a single menu cache entry
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: MenuCacheEntity)

    // 🔹 Insert or update a list of menu cache entries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MenuCacheEntity>)

    // 🔹 Get all cached items for a given branch
    @Query("SELECT * FROM menu_cache WHERE branchId = :branchId")
    suspend fun byBranch(branchId: String): List<MenuCacheEntity>

    // 🔹 Clear cache by branch
    @Query("DELETE FROM menu_cache WHERE branchId = :branchId")
    suspend fun clearBranch(branchId: String)

    // 🔹 Get by Firestore document id
    @Query("SELECT * FROM menu_cache WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): MenuCacheEntity?

    // 🔹 Reactive stream of all cached menus (for Live UI updates)
    @Query("SELECT * FROM menu_cache")
    fun getAll(): Flow<List<MenuCacheEntity>>

    // 🔹 Delete a single entity
    @Delete
    suspend fun delete(item: MenuCacheEntity)

    // 🔹 Delete by id
    @Query("DELETE FROM menu_cache WHERE id = :id")
    suspend fun deleteById(id: String)
}