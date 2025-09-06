package com.example.pizzamaniaapp.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.pizzamaniaapp.R;

public class CartFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // You can later replace with the full implementation (adapter + ViewModel)
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }
}