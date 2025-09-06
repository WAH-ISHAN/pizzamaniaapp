package com.example.pizzamaniaapp.app.ui.map;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.model.Branch;
import java.util.ArrayList;
import java.util.List;

public class BranchesMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private final List<Branch> branches = new ArrayList<>();

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        FirebaseFirestore.getInstance().collection("branches").get().addOnSuccessListener(snap -> {
            branches.clear();
            for (var d : snap.getDocuments()) {
                Branch b = d.toObject(Branch.class);
                if (b != null) { b.id = d.getId(); branches.add(b); }
            }
            if (map != null) renderMarkers();
        });
    }

    @Override public void onMapReady(GoogleMap gMap) {
        map = gMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        if (!branches.isEmpty()) renderMarkers();
    }

    private void renderMarkers() {
        LatLng sriLanka = new LatLng(7.8731, 80.7718);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sriLanka, 6.5f));
        for (Branch b : branches) {
            LatLng p = new LatLng(b.lat, b.lng);
            map.addMarker(new MarkerOptions().position(p).title(b.name).snippet(b.address));
        }
    }
}
