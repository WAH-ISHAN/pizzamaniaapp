package com.example.pizzamaniaapp.app.ui.menu;

import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.data.entities.MenuItem;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.VH> {
    public interface Callback { void onAddToCart(MenuItem item); void onOpenDetails(MenuItem item); }
    private List<MenuItem> data; private final Callback cb;
    public MenuAdapter(List<MenuItem> data, Callback cb) { this.data = data; this.cb = cb; }
    public void submit(List<MenuItem> d) { data = d; notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        MenuItem m = data.get(pos);
        h.title.setText(m.name);
        h.desc.setText(m.description);
        h.price.setText(String.format("Rs. %.2f", m.price));
        Glide.with(h.image).load(m.imageUrl).placeholder(R.drawable.placeholder).into(h.image);
        h.add.setOnClickListener(v -> cb.onAddToCart(m));
        h.itemView.setOnClickListener(v -> cb.onOpenDetails(m));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, desc, price; ImageView image; Button add;
        VH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            desc = v.findViewById(R.id.tvDesc);
            price = v.findViewById(R.id.tvPrice);
            image = v.findViewById(R.id.ivImage);
            add = v.findViewById(R.id.btnAdd);
        }
    }
}