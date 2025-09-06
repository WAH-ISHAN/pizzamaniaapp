package com.example.pizzamaniaapp.app.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import com.google.firebase.firestore.*;
import com.example.pizzamaniaapp.app.data.dao.MenuItemDao;
import com.example.pizzamaniaapp.app.data.entities.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuRepository {
    private final FirebaseFirestore db;
    private final MenuItemDao menuDao;

    public MenuRepository(FirebaseFirestore db, MenuItemDao menuDao) {
        this.db = db; this.menuDao = menuDao;
    }

    public LiveData<List<MenuItem>> observeMenu() {
        return menuDao.getAll();
    }

    // One-shot sync from Firestore -> Room
    public void refreshFromRemote() {
        db.collection("menu").get().addOnSuccessListener(snap -> {
            List<MenuItem> items = new ArrayList<>();
            for (DocumentSnapshot d : snap.getDocuments()) {
                MenuItem mi = d.toObject(MenuItem.class);
                if (mi != null) { mi.id = d.getId(); items.add(mi); }
            }
            AppExecutors.disk().execute(() -> menuDao.upsertAll(items));
        }).addOnFailureListener(e -> Log.e("MenuRepository", "Menu fetch failed", e));
    }
}