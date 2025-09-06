package com.example.pizzamaniaapp.app.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.pizzamaniaapp.app.data.local.AppDatabase;
import com.example.pizzamaniaapp.app.data.entities.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class SyncMenuWorker extends Worker {
    public SyncMenuWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull @Override
    public Result doWork() {
        try {
            List<MenuItem> items = new ArrayList<>();
            var snap = com.google.android.gms.tasks.Tasks.await(FirebaseFirestore.getInstance().collection("menu").get());
            for (var d : snap.getDocuments()) {
                MenuItem mi = d.toObject(MenuItem.class);
                if (mi != null) { mi.id = d.getId(); items.add(mi); }
            }
            AppDatabase.get(getApplicationContext()).menuItemDao().upsertAll(items);
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
