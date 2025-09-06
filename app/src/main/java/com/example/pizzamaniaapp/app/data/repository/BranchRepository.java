package com.example.pizzamaniaapp.app.data.repository;

import com.google.firebase.firestore.*;
import com.example.pizzamaniaapp.app.model.Branch;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BranchRepository {
    private final FirebaseFirestore db;
    public BranchRepository(FirebaseFirestore db) { this.db = db; }

    public CompletableFuture<List<Branch>> getAllBranches() {
        CompletableFuture<List<Branch>> f = new CompletableFuture<>();
        db.collection("branches").get().addOnSuccessListener(snap -> {
            List<Branch> list = new ArrayList<>();
            for (DocumentSnapshot d : snap.getDocuments()) {
                Branch b = d.toObject(Branch.class);
                if (b != null) { b.id = d.getId(); list.add(b); }
            }
            f.complete(list);
        }).addOnFailureListener(f::completeExceptionally);
        return f;
    }
}