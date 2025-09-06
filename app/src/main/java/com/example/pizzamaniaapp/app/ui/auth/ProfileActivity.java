package com.example.pizzamaniaapp.app.ui.auth;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.example.pizzamaniaapp.R;
import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {
    private ImageView img;
    private final ActivityResultLauncher<Void> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) uploadBitmap(bitmap);
            });

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        img = findViewById(R.id.ivProfile);
        Button btn = findViewById(R.id.btnCapture);
        btn.setOnClickListener(v -> takePicture.launch(null));
    }

    private void uploadBitmap(Bitmap bmp) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) { Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show(); return; }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();
        FirebaseStorage.getInstance().getReference("profiles/" + uid + ".jpg")
                .putBytes(data).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show();
                    img.setImageBitmap(bmp);
                }).addOnFailureListener(e -> Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}