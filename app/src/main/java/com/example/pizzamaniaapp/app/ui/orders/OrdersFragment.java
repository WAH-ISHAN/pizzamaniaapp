package com.example.pizzamaniaapp.app.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.model.Order;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrdersFragment extends Fragment {
    private RecyclerView rv; private TextView empty;
    private OrdersAdapter adapter;
    private ListenerRegistration reg;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders, container, false);
        rv = v.findViewById(R.id.recycler);
        empty = v.findViewById(R.id.emptyState);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrdersAdapter(new ArrayList<>(), order -> {
            Intent i = new Intent(getContext(), OrderDetailActivity.class);
            i.putExtra("orderId", order.id);
            startActivity(i);
        });
        rv.setAdapter(adapter);
        return v;
    }

    @Override public void onStart() {
        super.onStart();
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reg = db.collection("orders")
                .whereEqualTo("userId", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, err) -> {
                    if (err != null || snap == null) return;
                    List<Order> list = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Order o = d.toObject(Order.class);
                        if (o != null) { o.id = d.getId(); list.add(o); }
                    }
                    adapter.submit(list);
                    empty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    @Override public void onStop() {
        super.onStop();
        if (reg != null) { reg.remove(); reg = null; }
    }

    static class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {
        interface Callback { void onClick(Order order); }
        private List<Order> data; private final Callback cb;
        OrdersAdapter(List<Order> data, Callback cb) { this.data = data; this.cb = cb; }
        void submit(List<Order> d) { data = d; notifyDataSetChanged(); }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Order o = data.get(pos);
            h.id.setText("Order: " + o.id);
            h.status.setText("Status: " + (o.status==null?"PENDING":o.status));
            h.total.setText(String.format("Total: Rs. %.2f", o.total));
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(o.createdAt));
            h.date.setText(date);
            h.itemView.setOnClickListener(v -> cb.onClick(o));
        }

        @Override public int getItemCount() { return data==null?0:data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView id, status, total, date;
            VH(@NonNull View v) {
                super(v);
                id = v.findViewById(R.id.tvOrderId);
                status = v.findViewById(R.id.tvStatus);
                total = v.findViewById(R.id.tvTotal);
                date = v.findViewById(R.id.tvDate);
            }
        }
    }
}
