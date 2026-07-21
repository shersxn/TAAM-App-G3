package com.group3.taamapp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public abstract class BaseMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setOnBackPressedDispatcher();
        if (savedInstanceState == null) {
            loadFirstFragment();
        }
    }

    protected abstract int getLayoutId();

    protected void setOnBackPressedDispatcher() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    protected abstract void loadFirstFragment();

    protected void loadFragment(Fragment fragment, BundleInitializer bundleInitializer) {
        if(bundleInitializer != null) {
            Bundle bundle = new Bundle();
            bundleInitializer.initBundle(bundle);
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
