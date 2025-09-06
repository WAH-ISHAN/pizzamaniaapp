package com.example.pizzamaniaapp.app.ui.menu;

import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.data.entities.CartItem;
import com.example.pizzamaniaapp.app.data.entities.MenuItem;
import com.example.pizzamaniaapp.app.viewmodel.CartViewModel;
import com.example.pizzamaniaapp.app.viewmodel.MenuViewModel;
import java.util.ArrayList;

public class MenuFragment extends Fragment implements MenuAdapter.Callback {
    private MenuViewModel menuVm;
    private CartViewModel cartVm;
    private MenuAdapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        RecyclerView rv = v.findViewById(R.id.recycler);
        ProgressBar pb = v.findViewById(R.id.progress);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MenuAdapter(new ArrayList<>(), this);
        rv.setAdapter(adapter);

        menuVm = new ViewModelProvider(this).get(MenuViewModel.class);
        cartVm = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        menuVm.getMenu().observe(getViewLifecycleOwner(), list -> {
            adapter.submit(list);
            pb.setVisibility(View.GONE);
        });
        return v;
    }

    @Override public void onAddToCart(MenuItem item) {
        cartVm.addToCart(new CartItem(item.id, item.name, item.price, 1, item.imageUrl));
    }

    @Override public void onOpenDetails(MenuItem item) {
        // start detail activity if needed
    }
}