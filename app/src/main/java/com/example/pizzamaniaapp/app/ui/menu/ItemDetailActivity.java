package com.example.pizzamaniaapp.app.ui.menu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.pizzamaniaapp.R;

public class ItemDetailActivity extends AppCompatActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Item details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView iv = findViewById(R.id.ivImage);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvDesc = findViewById(R.id.tvDesc);
        TextView tvPrice = findViewById(R.id.tvPrice);

        String name = getIntent().getStringExtra("name");
        String desc = getIntent().getStringExtra("desc");
        double price = getIntent().getDoubleExtra("price", 0);
        String imageUrl = getIntent().getStringExtra("imageUrl");

        tvTitle.setText(name != null ? name : "");
        tvDesc.setText(desc != null ? desc : "");
        tvPrice.setText(String.format("Rs. %.2f", price));
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .into(iv);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}