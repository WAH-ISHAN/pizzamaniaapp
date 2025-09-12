package com.pizzamania.data.model

import com.google.firebase.Timestamp

data class Branch(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val address: String
)

data class MenuItem(
    val id: String,
    val branchId: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String?,
    val stock: Long
)

data class OrderItem(val menuId: String, val name: String, val price: Double, val qty: Int)

data class Order(
    val id: String,
    val userId: String,
    val branchId: String,
    val items: List<OrderItem>,
    val total: Double,
    val address: String,
    val phone: String,
    val payment: String,
    val status: String,
    val createdAt: Timestamp
)
