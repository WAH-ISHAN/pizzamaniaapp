package com.pizzamania.data.repo


import com.pizzamania.data.local.CartDao
import com.pizzamania.data.local.CartItemEntity
import com.pizzamania.data.model.MenuItem

class CartRepo(private val dao: CartDao) {
    suspend fun add(menu: MenuItem, qty: Int) {
        dao.insert(CartItemEntity(menuId = menu.id, name = menu.name, price = menu.price, imageUrl = menu.imageUrl, qty = qty))
    }
    suspend fun items(): List<CartItemEntity> = dao.all()
    suspend fun clear() = dao.clear()
}