package com.group3.taamapp;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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
        BottomNavigationView navigationBar = findViewById(R.id.navigationBar);
        navigationBar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                loadFragment(new HomeFragment(), null);
            }
            if (item.getItemId() == R.id.add) {
                loadFragment(new AddEditArtifactFragment(), null);
            }
            if (item.getItemId() == R.id.saved) {
                loadFragment(new SavedArtifactFragment(), null);
            }
            return false;
        });
        AuthModel model = new AuthModelFirebase(this);
        String email = model.getCurrentAccount();
        if(email == null) {
            loadFragment(new LoginFragment(), null);
            return;
        }
        loadFragment(new HomeFragment(), new BundleInitializer() {
            public void initBundle(Bundle bundle) {
                bundle.putString("email", email);
            }
        });
    }
}