package com.example.pizzamaniaapp.app.model;

public class OrderItem {
    public String menuItemId;
    public String name;
    public int qty;
    public double price;

    public OrderItem() {}
    public OrderItem(String menuItemId, String name, int qty, double price) {
        this.menuItemId = menuItemId; this.name = name; this.qty = qty; this.price = price;
    }
}