package com.example.pizzamaniaapp.app.model;


import java.util.List;

public class Order {
    public String id;
    public String userId;
    public String branchId;
    public double total;
    public String status; // PENDING, ACCEPTED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    public String paymentMethod; // COD, CARD_SIM
    public String address;
    public double lat;
    public double lng;
    public long createdAt;
    public List<OrderItem> items;

    public Order() {}
}