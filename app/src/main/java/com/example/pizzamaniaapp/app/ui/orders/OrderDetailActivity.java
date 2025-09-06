package com.example.pizzamaniaapp.app.ui.orders;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.data.repository.OrderRepository;
import com.example.pizzamaniaapp.app.model.Order;

public class OrderDetailActivity extends AppCompatActivity {
    private String orderId;
    private TextView tvOrderId, tvStatus, tvTotal, tvBranch, tvAddress;
    private OrderRepository repo;
    private ListenerRegistration statusReg;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        orderId = getIntent().getStringExtra("orderId");
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotal = findViewById(R.id.tvTotal);
        tvBranch = findViewById(R.id.tvBranch);
        tvAddress = findViewById(R.id.tvAddress);

        repo = new OrderRepository(getApplicationContext(), FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());
        tvOrderId.setText("Order: " + orderId);

        // Load details once
        FirebaseFirestore.getInstance().collection("orders").document(orderId)
                .get().addOnSuccessListener(d -> {
                    Order o = d.toObject(Order.class);
                    if (o == null) return;
                    tvStatus.setText("Status: " + o.status);
                    tvTotal.setText(String.format("Total: Rs. %.2f", o.total));
                    tvBranch.setText("Branch: " + o.branchId);
                    tvAddress.setText("Address: " + o.address);
                }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load order", Toast.LENGTH_SHORT).show());

        // Listen status changes
        statusReg = repo.listenOrderStatus(orderId, status -> runOnUiThread(() -> tvStatus.setText("Status: " + status)));
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (statusReg != null) { statusReg.remove(); }
    }
}