package com.example.pizzamaniaapp.app.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.example.pizzamaniaapp.app.data.entities.CartItem;
import com.example.pizzamaniaapp.app.model.Branch;
import com.example.pizzamaniaapp.app.model.Order;
import com.example.pizzamaniaapp.app.model.OrderItem;
import com.example.pizzamaniaapp.app.utils.DistanceUtils;
import com.example.pizzamaniaapp.app.utils.NotificationUtils;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class OrderRepository {
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final Context appCtx;

    public OrderRepository(Context appCtx, FirebaseFirestore db, FirebaseAuth auth) {
        this.appCtx = appCtx.getApplicationContext();
        this.db = db; this.auth = auth;
    }

    public CompletableFuture<String> placeOrder(double userLat, double userLng, String address, String paymentMethod, List<CartItem> cart, List<Branch> branches) {
        CompletableFuture<String> future = new CompletableFuture<>();
        if (auth.getCurrentUser() == null) {
            future.completeExceptionally(new IllegalStateException("Not logged in"));
            return future;
        }
        String uid = auth.getCurrentUser().getUid();

        // Build order items
        List<OrderItem> items = new ArrayList<>();
        double total = 0;
        Map<String, Integer> req = new HashMap<>();
        for (CartItem c : cart) {
            items.add(new OrderItem(c.menuItemId, c.name, c.quantity, c.price));
            total += c.price * c.quantity;
            req.put(c.menuItemId, req.getOrDefault(c.menuItemId, 0) + c.quantity);
        }

        // Choose nearest branch with stock
        Branch chosen = null;
        for (Branch b : branches) {
            if (hasAllStock(b.id, req)) {
                if (chosen == null) chosen = b;
                else {
                    double d1 = DistanceUtils.distanceKm(userLat, userLng, chosen.lat, chosen.lng);
                    double d2 = DistanceUtils.distanceKm(userLat, userLng, b.lat, b.lng);
                    if (d2 < d1) chosen = b;
                }
            }
        }
        if (chosen == null) {
            future.completeExceptionally(new IllegalStateException("No branch has enough stock for this order."));
            return future;
        }

        // Create order document and decrement stock in a transaction
        DocumentReference orderRef = db.collection("orders").document();
        String orderId = orderRef.getId();
        String branchId = chosen.id;
        double finalTotal = total;

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // Check stock and decrement
            for (Map.Entry<String, Integer> e : req.entrySet()) {
                DocumentReference stockRef = db.collection("stocks").document(branchId)
                        .collection("items").document(e.getKey());
                DocumentSnapshot stockSnap = transaction.get(stockRef);
                long qty = 0;
                if (stockSnap.exists() && stockSnap.contains("quantity")) {
                    qty = stockSnap.getLong("quantity");
                }
                if (qty < e.getValue()) {
                    throw new FirebaseFirestoreException("Out of stock during transaction",
                            FirebaseFirestoreException.Code.ABORTED);
                }
                transaction.update(stockRef, "quantity", qty - e.getValue());
            }

            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("id", orderId);
            orderMap.put("userId", uid);
            orderMap.put("branchId", branchId);
            orderMap.put("total", finalTotal);
            orderMap.put("status", "PENDING");
            orderMap.put("paymentMethod", paymentMethod);
            orderMap.put("address", address);
            orderMap.put("lat", userLat);
            orderMap.put("lng", userLng);
            orderMap.put("createdAt", System.currentTimeMillis());
            orderMap.put("items", items);
            transaction.set(orderRef, orderMap);

            return null;
        }).addOnSuccessListener(v -> {
            NotificationUtils.notifyOrder(appCtx, orderId.hashCode(), "Order placed", "Your order " + orderId + " is pending.");
            future.complete(orderId);
        }).addOnFailureListener(future::completeExceptionally);

        return future;
    }

    private boolean hasAllStock(@NonNull String branchId, @NonNull Map<String, Integer> req) {
        try {
            // read all needed documents
            for (Map.Entry<String, Integer> e : req.entrySet()) {
                DocumentSnapshot snap = Tasks.await(db.collection("stocks").document(branchId)
                        .collection("items").document(e.getKey()).get());
                long qty = 0;
                if (snap.exists() && snap.contains("quantity")) qty = snap.getLong("quantity");
                if (qty < e.getValue()) return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ListenerRegistration listenOrderStatus(String orderId, StatusListener listener) {
        return db.collection("orders").document(orderId)
                .addSnapshotListener((snap, err) -> {
                    if (err != null || snap == null || !snap.exists()) return;
                    String status = snap.getString("status");
                    listener.onStatus(status);
                    NotificationUtils.notifyOrder(appCtx, orderId.hashCode(), "Order update", "Status: " + status);
                });
    }

    public interface StatusListener { void onStatus(String status); }
}
