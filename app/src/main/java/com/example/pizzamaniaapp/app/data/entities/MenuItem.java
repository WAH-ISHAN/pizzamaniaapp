package com.example.pizzamaniaapp.app.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "menu_items")
public class MenuItem {
    @PrimaryKey @NonNull
    public String id;
    public String name;
    public String description;
    public String category; // Pizza, Sides, Drinks, Desserts
    public double price;
    public String imageUrl; // can be Firebase Storage or drawable name
    public boolean veg;

    public MenuItem(@NonNull String id, String name, String description,
                    String category, double price, String imageUrl, boolean veg) {
        this.id = id; this.name = name; this.description = description;
        this.category = category; this.price = price; this.imageUrl = imageUrl; this.veg = veg;
    }
}
