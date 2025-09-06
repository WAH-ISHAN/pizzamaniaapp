package com.example.pizzamaniaapp.app.utils;

import android.app.*;
import android.content.Context;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import com.example.pizzamaniaapp.R;

public class NotificationUtils {
    public static final String CHANNEL_ORDERS = "orders_channel";

    public static void ensureChannels(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ORDERS, "Order Updates", NotificationManager.IMPORTANCE_DEFAULT);
            ch.setDescription("Order status notifications");
            ch.enableLights(true);
            ch.setLightColor(Color.GREEN);
            NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }
    }

    public static void notifyOrder(Context ctx, int id, String title, String text) {
        ensureChannels(ctx);
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ORDERS)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, b.build());
    }
}