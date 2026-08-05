package com.group3.taamapp.Model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group3.taamapp.ExpandedArtifact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SavedArtifactModelFirebase implements SavedArtifactModel {
    private final DatabaseReference savedRef;
    private final DatabaseReference artifactsRef;
    private static final String FIELD_COLLECTIONS = "collections";

    public SavedArtifactModelFirebase(Context context) {
        FirebaseDatabase db = FirebaseDatabase.getInstance(
                "https://cscb07-group3-taamapp-default-rtdb.firebaseio.com/"
        );

        savedRef = db.getReference("Users");
        artifactsRef = db.getReference("Artifacts");
    }

    // Firebase keys cannot contain '.', '#', '$', '[' or ']', so emails must be
    // encoded before being used as a path segment.
    private String encodeEmailKey(String email) {
        return email.replace(".", ",");
    }

    @Override
    public void getSavedArtifacts(String userEmail, SavedArtifactsCallback callback) {
        if (callback == null) {
                throw new NullPointerException(
                        "SavedArtifactModelFirebase.getSavedArtifacts: callback cannot be null"
                );
            }

        if (userEmail == null || userEmail.isEmpty()) {
            callback.onError("No logged-in user");
            return;
        }

        String userKey = encodeEmailKey(userEmail);
        savedRef.child(userKey)
        .child(FIELD_COLLECTIONS)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Collect all saved artifact IDs (lot numbers) for this user.
                List<String> savedLotNumbers = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    savedLotNumbers.add(child.getKey());
                }

                if (savedLotNumbers.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                List<ExpandedArtifact> results = new ArrayList<>();
                AtomicInteger remaining = new AtomicInteger(savedLotNumbers.size());

                for (String lotNumber : savedLotNumbers) {
                    artifactsRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot artifactSnapshot) {
                            ExpandedArtifact artifact = artifactSnapshot.getValue(ExpandedArtifact.class);
                            if (artifact != null) {
                                // Multiple Firebase callbacks may complete at the same time, so synchronize access to the shared results list.
                                synchronized (results) {
                                    results.add(artifact);
                                }
                            }
                            // Callback only after every artifact has finished loading.
                            if (remaining.decrementAndGet() == 0) {
                                callback.onSuccess(results);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (remaining.decrementAndGet() == 0) {
                                callback.onSuccess(results);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Firebase connection cancelled");
            }
        });
    }

        @Override
        public void saveArtifact(String userEmail, String lotNumber, SaveCallback callback) {
            if (userEmail == null) {
                throw new NullPointerException("SavedArtifactModelFirebase.saveArtifact: userEmail cannot be null");
            }
            if (lotNumber == null) {
                throw new NullPointerException("SavedArtifactModelFirebase.saveArtifact: lotNumber cannot be null");
            }
            if (callback == null) {
                throw new NullPointerException("SavedArtifactModelFirebase.saveArtifact: callback cannot be null");
            }

            String userKey = encodeEmailKey(userEmail);
            savedRef.child(userKey).child(FIELD_COLLECTIONS).child(lotNumber).setValue(true)
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }

        @Override
        public void unsaveArtifact(String userEmail, String lotNumber, UnsaveCallback callback) {
            if (userEmail == null) {
                throw new NullPointerException("SavedArtifactModelFirebase.unsaveArtifact: userEmail cannot be null");
            }
            if (lotNumber == null) {
                throw new NullPointerException("SavedArtifactModelFirebase.unsaveArtifact: lotNumber cannot be null");
            }
            if (callback == null) {
                throw new NullPointerException("SavedArtifactModelFirebase.unsaveArtifact: callback cannot be null");
            }

            String userKey = encodeEmailKey(userEmail);
            savedRef.child(userKey).child(FIELD_COLLECTIONS).child(lotNumber).removeValue()
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
        }
}
