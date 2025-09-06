package com.example.pizzamaniaapp.app.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.data.entities.CartItem;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.VH> {
    public interface Callback { void onInc(CartItem i); void onDec(CartItem i); void onRemove(CartItem i); }
    private List<CartItem> data; private final Callback cb;
    public CartListAdapter(List<CartItem> data, Callback cb) { this.data = data; this.cb = cb; }
    public void submit(List<CartItem> d) { data = d; notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        CartItem c = data.get(pos);
        h.name.setText(c.name);
        h.price.setText(String.format("Rs. %.2f", c.price));
        h.qty.setText(String.valueOf(c.quantity));
        Glide.with(h.thumb).load(c.imageUrl).placeholder(R.drawable.placeholder).into(h.thumb);
        h.plus.setOnClickListener(v -> cb.onInc(c));
        h.minus.setOnClickListener(v -> cb.onDec(c));
        h.remove.setOnClickListener(v -> cb.onRemove(c));
    }

    @Override public int getItemCount() { return data == null ? 0 : data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb; TextView name, price, qty; Button plus, minus, remove;
        VH(@NonNull View v) {
            super(v);
            thumb = v.findViewById(R.id.ivThumb);
            name = v.findViewById(R.id.tvName);
            price = v.findViewById(R.id.tvPrice);
            qty = v.findViewById(R.id.tvQty);
            plus = v.findViewById(R.id.btnPlus);
            minus = v.findViewById(R.id.btnMinus);
            remove = v.findViewById(R.id.btnRemove);
        }
    }
}