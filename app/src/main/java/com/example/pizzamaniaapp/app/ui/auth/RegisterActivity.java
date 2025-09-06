package com.example.pizzamaniaapp.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.pizzamaniaapp.R;
import com.example.pizzamaniaapp.app.ui.main.MainActivity;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText name = findViewById(R.id.etName);
        EditText email = findViewById(R.id.etEmail);
        EditText pass = findViewById(R.id.etPassword);
        Button btn = findViewById(R.id.btnRegister);

        btn.setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String p = pass.getText().toString();
            if (n.isEmpty() || e.isEmpty() || p.length() < 6) {
                Toast.makeText(this, "Fill all fields (password >= 6)", Toast.LENGTH_SHORT).show(); return;
            }
            btn.setEnabled(false);
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(e, p).addOnCompleteListener(t -> {
                btn.setEnabled(true);
                if (t.isSuccessful()) {
                    String uid = t.getResult().getUser().getUid();
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", n); user.put("email", e);
                    FirebaseFirestore.getInstance().collection("users").document(uid).set(user);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Register failed: " + t.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}