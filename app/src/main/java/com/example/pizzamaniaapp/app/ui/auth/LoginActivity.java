package com.example.pizzamaniaapp.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        EditText email = findViewById(R.id.etEmail);
        EditText pass = findViewById(R.id.etPassword);
        Button btn = findViewById(R.id.btnLogin);
        TextView goReg = findViewById(R.id.tvGoRegister);
        btn.setOnClickListener(v -> {
            String e = email.getText().toString().trim();
            String p = pass.getText().toString();
            if (e.isEmpty() || p.isEmpty()) { Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show(); return; }
            btn.setEnabled(false);
            auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(task -> {
                btn.setEnabled(true);
                if (task.isSuccessful()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Login failed: " + (task.getException()!=null?task.getException().getMessage():""), Toast.LENGTH_LONG).show();
                }
            });
        });
        goReg.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}