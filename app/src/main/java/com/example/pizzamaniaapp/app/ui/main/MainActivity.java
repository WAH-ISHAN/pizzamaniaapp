package com.example.pizzamaniaapp.app.ui.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.ui.cart.CartFragment;
import com.example.pizzamaniaapp.app.ui.home.HomeFragment;
import com.example.pizzamaniaapp.app.ui.menu.MenuFragment;
import com.example.pizzamaniaapp.app.ui.orders.OrdersFragment;

public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            Fragment f;
            int id = item.getItemId();
            if (id == R.id.menu) f = new MenuFragment();
            else if (id == R.id.cart) f = new CartFragment();
            else if (id == R.id.orders) f = new OrdersFragment();
            else f = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, f).commit();
            return true;
        });
        nav.setSelectedItemId(R.id.home);
    }
}