package com.example.pizzamaniaapp.app.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String menuItemId;
    public String name;
    public double price;
    public int quantity;
    public String imageUrl;

    public CartItem(String menuItemId, String name, double price, int quantity, String imageUrl) {
        this.menuItemId = menuItemId; this.name = name; this.price = price; this.quantity = quantity; this.imageUrl = imageUrl;
    }
}