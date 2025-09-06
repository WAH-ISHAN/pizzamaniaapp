package com.example.pizzamaniaapp.app;

import android.app.Application;
import androidx.work.*;
import com.example.pizzamaniaapp.app.workers.SyncMenuWorker;
import java.util.concurrent.TimeUnit;
public class app extends Application {
    @Override public void onCreate() {
        super.onCreate();
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(SyncMenuWorker.class, 6, TimeUnit.HOURS)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("menu_sync", ExistingPeriodicWorkPolicy.KEEP, req);
    }
}