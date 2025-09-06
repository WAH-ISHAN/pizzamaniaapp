package com.example.pizzamaniaapp.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.pizzamaniaapp.app.data.entities.CartItem;
import com.example.pizzamaniaapp.app.data.local.AppDatabase;
import com.example.pizzamaniaapp.app.data.repository.CartRepository;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository repo;
    private final LiveData<List<CartItem>> cart;
    private final LiveData<Double> total;

    public CartViewModel(@NonNull Application app) {
        super(app);
        repo = new CartRepository(AppDatabase.get(app).cartItemDao());
        cart = repo.observeCart();
        total = repo.observeTotal();
    }

    public LiveData<List<CartItem>> getCart() { return cart; }
    public LiveData<Double> getTotal() { return total; }
    public void addToCart(CartItem i) { repo.add(i); }
    public void update(CartItem i) { repo.update(i); }
    public void remove(CartItem i) { repo.remove(i); }
    public void clear() { repo.clear(); }
}