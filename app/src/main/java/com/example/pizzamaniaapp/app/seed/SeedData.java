package com.example.pizzamaniaapp.app.seed;


import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SeedData {
    public static void seed(FirebaseFirestore db) {
        Map<String,Object> colombo = new HashMap<>();
        colombo.put("name","Colombo Branch");
        colombo.put("address","123 Galle Rd, Colombo");
        colombo.put("lat", 6.9271);
        colombo.put("lng", 79.8612);
        colombo.put("phone","+94 11 123 4567");
        var colRef = db.collection("branches").document("colombo");
        colRef.set(colombo);

        Map<String,Object> galle = new HashMap<>();
        galle.put("name","Galle Branch");
        galle.put("address","45 Fort, Galle");
        galle.put("lat", 6.0535);
        galle.put("lng", 80.2210);
        galle.put("phone","+94 91 765 4321");
        var gaRef = db.collection("branches").document("galle");
        gaRef.set(galle);

        // Menu
        var m1 = new HashMap<String,Object>() {{ put("name","Chicken Pizza"); put("description","Grilled chicken, mozzarella"); put("category","Pizza"); put("price", 1890.0); put("imageUrl","https://.../chicken.jpg"); put("veg", false); }};
        var m2 = new HashMap<String,Object>() {{ put("name","Veggie Delight"); put("description","Bell pepper, olive, onion"); put("category","Pizza"); put("price", 1690.0); put("imageUrl","https://.../veg.jpg"); put("veg", true); }};
        db.collection("menu").document("chicken_pizza").set(m1);
        db.collection("menu").document("veggie_delight").set(m2);

        // Stocks per branch
        db.collection("stocks").document("colombo").collection("items").document("chicken_pizza").set(new HashMap<String,Object>(){{ put("quantity", 50); }});
        db.collection("stocks").document("colombo").collection("items").document("veggie_delight").set(new HashMap<String,Object>(){{ put("quantity", 50); }});
        db.collection("stocks").document("galle").collection("items").document("chicken_pizza").set(new HashMap<String,Object>(){{ put("quantity", 30); }});
        db.collection("stocks").document("galle").collection("items").document("veggie_delight").set(new HashMap<String,Object>(){{ put("quantity", 60); }});
    }
}