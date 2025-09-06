package com.example.pizzamaniaapp.app.ui.checkout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.data.entities.CartItem;
import com.example.pizzamaniaapp.app.data.local.AppDatabase;
import com.example.pizzamaniaapp.app.data.repository.*;
import com.example.pizzamaniaapp.app.model.Branch;
import com.example.pizzamaniaapp.app.viewmodel.CartViewModel;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private FusedLocationProviderClient fused;
    private double lat, lng;
    private TextView locationTv;
    private EditText addressEt;
    private RadioGroup payGroup;
    private Button placeBtn;

    private CartViewModel cartVm;
    private BranchRepository branchRepo;
    private OrderRepository orderRepo;

    private final ActivityResultLauncher<String> permLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) detectLocation();
                else Toast.makeText(this, "Location permission needed to auto-select branch", Toast.LENGTH_SHORT).show();
            });

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        fused = LocationServices.getFusedLocationProviderClient(this);

        locationTv = findViewById(R.id.tvLocation);
        addressEt = findViewById(R.id.etAddress);
        payGroup = findViewById(R.id.rgPayment);
        placeBtn = findViewById(R.id.btnPlaceOrder);

        cartVm = new ViewModelProvider(this).get(CartViewModel.class);
        branchRepo = new BranchRepository(FirebaseFirestore.getInstance());
        orderRepo = new OrderRepository(getApplicationContext(), FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());

        permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        placeBtn.setOnClickListener(v -> {
            List<CartItem> cart = cartVm.getCart().getValue();
            if (cart == null || cart.isEmpty()) { Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show(); return; }
            String address = addressEt.getText().toString().trim();
            if (address.isEmpty()) { Toast.makeText(this, "Enter address", Toast.LENGTH_SHORT).show(); return; }
            int selected = payGroup.getCheckedRadioButtonId();
            String method = selected == R.id.rbCard ? "CARD_SIM" : "COD";

            placeBtn.setEnabled(false);
            branchRepo.getAllBranches().thenCompose(branches ->
                    orderRepo.placeOrder(lat, lng, address, method, cart, branches)
            ).whenComplete((orderId, ex) -> runOnUiThread(() -> {
                placeBtn.setEnabled(true);
                if (ex != null) {
                    Toast.makeText(this, "Order failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Order placed: " + orderId, Toast.LENGTH_LONG).show();
                    cartVm.clear();
                    finish();
                }
            }));
        });
    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        fused.getLastLocation().addOnSuccessListener(loc -> {
            if (loc != null) updateLocation(loc);
            else {
                LocationRequest req = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).setMaxUpdates(1).build();
                fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this::updateLocation);
            }
        });
    }

    private void updateLocation(Location loc) {
        lat = loc.getLatitude(); lng = loc.getLongitude();
        locationTv.setText(String.format("Lat: %.5f, Lng: %.5f", lat, lng));
    }
}
