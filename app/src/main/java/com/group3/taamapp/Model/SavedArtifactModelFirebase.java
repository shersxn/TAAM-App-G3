package com.group3.taamapp.Model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.group3.taamapp.ExpandedArtifact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SavedArtifactModelFirebase implements SavedArtifactModel {
    private final DatabaseReference savedRef;
    private final DatabaseReference artifactsRef;

    public SavedArtifactModelFirebase(Context context) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-group3-taamapp-default-rtdb.firebaseio.com/");
        savedRef = db.getReference("Saved");
        artifactsRef = db.getReference("Artifacts");
    }

    // Firebase keys cannot contain '.', '#', '$', '[' or ']', so emails must be
    // encoded before being used as a path segment.
    private String encodeEmailKey(String email) {
        return email.replace(".", ",");
    }

    @Override
    public void getSavedArtifacts(String userEmail, SavedArtifactsCallback callback) {
        if (userEmail == null || userEmail.isEmpty()) {
            callback.onError("No logged-in user was provided");
            return;
        }
        if (callback == null) {
            throw new NullPointerException("SavedArtifactModelFirebase.getSavedArtifacts: callback cannot be null");
        }

        String userKey = encodeEmailKey(userEmail);
        savedRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                synchronized (results) {
                                    results.add(artifact);
                                }
                            }
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
        savedRef.child(userKey).child(lotNumber).setValue(true)
                .addOnSuccessListener(unused -> {
                    // Best-effort save-count increment, does not block the save itself
                    artifactsRef.child(lotNumber).child("saveCount").runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Integer current = currentData.getValue(Integer.class);
                            currentData.setValue(current == null ? 1 : current + 1);
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentState) {
                            // no-op: saveCount is a nice-to-have display value
                        }
                    });
                    callback.onSuccess();
                })
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
        savedRef.child(userKey).child(lotNumber).removeValue()
                .addOnSuccessListener(unused -> {
                    // Best-effort save-count decrement; does not block the unsave itself
                    artifactsRef.child(lotNumber).child("saveCount").runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            Integer current = currentData.getValue(Integer.class);
                            int updated = (current == null ? 0 : current - 1);
                            currentData.setValue(Math.max(updated, 0));
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentState) {
                            // no-op: saveCount is a nice-to-have display value
                        }
                    });
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
