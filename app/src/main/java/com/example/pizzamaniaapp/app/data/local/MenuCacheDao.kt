package com.pizzamania.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuCacheDao {

    /** 🔹 Insert or update single menu item (used by MenuRepo) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: MenuCacheEntity)

    /** 🔹 Insert or update a list of menu items */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MenuCacheEntity>)

    /** 🔹 Get all menus for a given branch */
    @Query("SELECT * FROM menu_cache WHERE branchId = :branchId")
    suspend fun byBranch(branchId: String): List<MenuCacheEntity>

    /** 🔹 Clear cache for a given branch */
    @Query("DELETE FROM menu_cache WHERE branchId = :branchId")
    suspend fun clearBranch(branchId: String)

    /** 🔹 Insert single item (alias for insertOrUpdate) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(menu: MenuCacheEntity)

    /** 🔹 Find a single item by id */
    @Query("SELECT * FROM menu_cache WHERE id = :id")
    suspend fun getById(id: String): MenuCacheEntity?

    /** 🔹 Get all menus (stream with Flow for observation) */
    @Query("SELECT * FROM menu_cache")
    fun getAll(): Flow<List<MenuCacheEntity>>

    /** 🔹 Delete a specific item */
    @Delete
    suspend fun delete(menu: MenuCacheEntity)
}