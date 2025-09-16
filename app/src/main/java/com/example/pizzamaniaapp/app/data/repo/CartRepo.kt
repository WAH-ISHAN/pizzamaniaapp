package com.pizzamania.data.repo

import com.pizzamania.data.local.CartDao
import com.pizzamania.data.local.CartItemEntity
import com.pizzamania.data.model.MenuItem

class CartRepo(private val dao: CartDao) {

    /** Add new item or update quantity if it already exists */
    suspend fun add(menu: MenuItem, qty: Int) {
        val existing = dao.findByMenuId(menu.id)
        if (existing != null) {
            dao.insert(existing.copy(qty = existing.qty + qty))
        } else {
            dao.insert(
                CartItemEntity(
                    menuId = menu.id,
                    name = menu.name,
                    price = menu.price,
                    imageUrl = menu.imageUrl,
                    qty = qty
                )
            )
        }
    }

    /** Increase quantity (e.g. when + button is pressed) */
    suspend fun increaseQty(menuId: String) {
        val existing = dao.findByMenuId(menuId) ?: return
        dao.insert(existing.copy(qty = existing.qty + 1))
    }

    /** Decrease quantity or remove if reaches 0 */
    suspend fun decreaseQty(menuId: String) {
        val existing = dao.findByMenuId(menuId) ?: return
        if (existing.qty > 1) {
            dao.insert(existing.copy(qty = existing.qty - 1))
        } else {
            dao.delete(existing)
        }
    }

    /** Remove an item completely regardless of qty */
    suspend fun remove(menuId: String) {
        val existing = dao.findByMenuId(menuId) ?: return
        dao.delete(existing)
    }

    /** Get all items */
    suspend fun items(): List<CartItemEntity> = dao.all()

    /** Calculate total count (for UI badges) */
    suspend fun totalItems(): Int = dao.all().sumOf { it.qty }

    /** Calculate total price */
    suspend fun totalPrice(): Double = dao.all().sumOf { it.price * it.qty }

    /** Empty the cart */
    suspend fun clear() = dao.clear()
}