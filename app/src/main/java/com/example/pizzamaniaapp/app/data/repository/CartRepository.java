package com.example.pizzamaniaapp.app.data.repository;

import androidx.lifecycle.LiveData;
import com.example.pizzamaniaapp.app.data.dao.CartItemDao;
import com.example.pizzamaniaapp.app.data.entities.CartItem;
import java.util.List;

public class CartRepository {
    private final CartItemDao dao;
    public CartRepository(CartItemDao dao) { this.dao = dao; }

    public LiveData<List<CartItem>> observeCart() { return dao.observeCart(); }
    public LiveData<Double> observeTotal() { return dao.observeTotal(); }

    public void add(CartItem item) { AppExecutors.disk().execute(() -> dao.insert(item)); }
    public void update(CartItem item) { AppExecutors.disk().execute(() -> dao.update(item)); }
    public void remove(CartItem item) { AppExecutors.disk().execute(() -> dao.delete(item)); }
    public void clear() { AppExecutors.disk().execute(dao::clear); }
}