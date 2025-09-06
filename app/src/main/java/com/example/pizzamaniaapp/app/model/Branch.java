package com.example.pizzamaniaapp.app.model;

public class Branch {
    public String id;
    public String name;
    public String address;
    public double lat;
    public double lng;
    public String phone;

    public Branch() {}
    public Branch(String id, String name, String address, double lat, double lng, String phone) {
        this.id = id; this.name = name; this.address = address; this.lat = lat; this.lng = lng; this.phone = phone;
    }
}