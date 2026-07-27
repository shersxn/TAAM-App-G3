package com.group3.taamapp;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteArtifactHelper {

    private final DatabaseReference databaseRoot;

    public DeleteArtifactHelper() {
        this.databaseRoot = FirebaseDatabase.getInstance().getReference();
    }

    public interface AdminCallback { void onResult(boolean isAdmin, String message); }
    public interface FetchCallback { void onFetched(List<String> names, String error); }
    public interface DeleteCallback { void onComplete(boolean success, String message); }

    // Check Admin Status using the provided email
    public void verifyAdminStatus(String userEmail, AdminCallback callback) {
        if (userEmail == null || userEmail.isEmpty()) {
            callback.onResult(false, "No user is currently logged in.");
            return;
        }

        String safeEmail = userEmail.replace(".", ",");

        databaseRoot.child("Users").child(safeEmail).child("admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Get value attached to email key, 1 showing they are an admin
                        Long adminValue = snapshot.getValue(Long.class);
                        if (adminValue != null && adminValue == 1L) {
                            callback.onResult(true, "Admin verified.");
                        } else {
                            callback.onResult(false, "Access Denied: You are not an admin.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(false, error.getMessage());
                    }
                });
    }

    // Add all artifact names from the database into the dropdown menu
    public void fetchArtifactNames(FetchCallback callback) {
        databaseRoot.child("Artifacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> names = new ArrayList<>();
                for (DataSnapshot artifactSnapshot : snapshot.getChildren()) {
                    String name = artifactSnapshot.child("name").getValue(String.class);
                    if (name != null) {
                        names.add(name);
                    }
                }
                callback.onFetched(names, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFetched(null, error.getMessage());
            }
        });
    }

    // Find the artifact from the database and delete it by name
    public void deleteArtifactByName(String name, DeleteCallback callback) {
        Query query = databaseRoot.child("Artifacts").orderByChild("name").equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot match : snapshot.getChildren()) {
                        // Delete the specific lot number node
                        match.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> callback.onComplete(true, name + " deleted successfully!"))
                                .addOnFailureListener(e -> callback.onComplete(false, "Failed to delete: " + e.getMessage()));
                    }
                } else {
                    callback.onComplete(false, "Artifact not found in database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onComplete(false, error.getMessage());
            }
        });
    }
}