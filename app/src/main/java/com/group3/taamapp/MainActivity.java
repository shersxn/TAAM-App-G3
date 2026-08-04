package com.group3.taamapp;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group3.taamapp.Bases.BaseMainActivity;
import com.group3.taamapp.LoginPage.LoginFragment;
import com.group3.taamapp.Model.AuthModel;
import com.group3.taamapp.Model.AuthModelFirebase;
import com.group3.taamapp.Bases.BundleInitializer;

public class MainActivity extends BaseMainActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void loadFirstFragment() {
        AuthModel model = new AuthModelFirebase(this);
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cscb07-group3-taamapp-default-rtdb.firebaseio.com/");
        String email = model.getCurrentAccount();
        BottomNavigationView navigationBar = findViewById(R.id.navigationBar);

        navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                loadFragment(new HomeFragment(), new BundleInitializer() {
                    public void initBundle(Bundle bundle) {
                        bundle.putString("email", email);
                    }
                });
            }
            if (item.getItemId() == R.id.add) {
                loadFragment(new AddEditArtifactFragment(), new BundleInitializer() {
                    @Override
                    public void initBundle(Bundle bundle) {
                        bundle.putString("userEmail", email);
                    }
                });
            }
            if (item.getItemId() == R.id.saved) {
                loadFragment(new SavedArtifactFragment(), new BundleInitializer() {
                    @Override
                    public void initBundle(Bundle bundle) {
                        bundle.putString("email", email);
                    }
                });
            }
            return false;
        });
        if(email == null) {
            loadFragment(new LoginFragment(), null);
            return;
        }
        else {
            DatabaseReference userRef = db.getReference("Users").child(email);
            Menu menu = navigationBar.getMenu();

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isAdmin = snapshot.child("admin").getValue(boolean.class);
                    if (isAdmin) {
                        menu.findItem(R.id.add).setVisible(true);
                    }
                    else {
                        menu.findItem(R.id.add).setVisible(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        loadFragment(new HomeFragment(), new BundleInitializer() {
            public void initBundle(Bundle bundle) {
                bundle.putString("email", email);
            }
        });
    }
}