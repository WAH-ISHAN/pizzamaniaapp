package com.pizzamania.data.model

import com.google.firebase.Timestamp
import androidx.annotation.Keep

// --- Branch Model ---
@Keep
data class Branch(
    val id: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = ""
)

// --- Menu Item Model ---
@Keep
data class MenuItem(
    val id: String = "",
    val branchId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val stock: Long = 0L,
    val toppings: List<String> = emptyList()   // ✅ provides default empty list
)

// --- OrderItem (line inside an Order) ---
@Keep
data class OrderItem(
    val menuId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val qty: Int = 0
)

// --- Order Model ---
@Keep
data class Order(
    val id: String = "",
    val userId: String = "",
    val branchId: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val address: String = "",
    val phone: String = "",
    val payment: String = "COD",
    val status: String = "Pending",
    val createdAt: Timestamp = Timestamp.now()   // ✅ sensible default
)