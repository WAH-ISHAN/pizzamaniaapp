package com.example.pizzamaniaapp.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.pizzamaniaapp.app.data.local.AppDatabase;
import com.example.pizzamaniaapp.app.data.repository.MenuRepository;
import com.example.pizzamaniaapp.app.data.entities.MenuItem;
import java.util.List;

public class MenuViewModel extends AndroidViewModel {
    private final MenuRepository repo;
    private final LiveData<List<MenuItem>> menu;

    public MenuViewModel(@NonNull Application app) {
        super(app);
        repo = new MenuRepository(FirebaseFirestore.getInstance(), AppDatabase.get(app).menuItemDao());
        menu = repo.observeMenu();
        repo.refreshFromRemote(); // initial load
    }

    public LiveData<List<MenuItem>> getMenu() { return menu; }
    public void refresh() { repo.refreshFromRemote(); }
}