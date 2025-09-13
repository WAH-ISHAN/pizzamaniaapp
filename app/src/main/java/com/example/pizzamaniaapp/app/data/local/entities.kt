package com.pizzamania.data.local

import androidx.room.*

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val menuId: String,
    val name: String,
    val price: Double,
    val imageUrl: String?,
    val qty: Int
)

@Entity(tableName = "menu_cache")
data class MenuCacheEntity(
    @PrimaryKey val id: String,
    val branchId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Long
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items") suspend fun all(): List<CartItemEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: CartItemEntity)
    @Update suspend fun update(item: CartItemEntity)
    @Delete suspend fun delete(item: CartItemEntity)
    @Query("DELETE FROM cart_items") suspend fun clear()
}

@Dao
interface MenuCacheDao {
    @Query("SELECT * FROM menu_cache WHERE branchId=:branchId") suspend fun byBranch(branchId: String): List<MenuCacheEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(items: List<MenuCacheEntity>)
    @Query("DELETE FROM menu_cache WHERE branchId=:branchId") suspend fun clearBranch(branchId: String)
}

@Database(entities = [CartItemEntity::class, MenuCacheEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun menuCacheDao(): MenuCacheDao
}
