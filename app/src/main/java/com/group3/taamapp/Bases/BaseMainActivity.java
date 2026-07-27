package com.group3.taamapp.Bases;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.group3.taamapp.R;

/**
 * Template MainActivity, need to define
 * - getLayoutId
 * - loadFirstFragment
 */
public abstract class BaseMainActivity extends AppCompatActivity {
    /**
     * Template for onCreate, defined by
     * - getLayoutId
     * - loadFirstFragment
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        setOnBackPressedDispatcher();
        if (savedInstanceState == null) {
            loadFirstFragment();
        }
    }

    /**
     * Return the layout id of main activity, used for the template of onCreate defined in BaseMainActivity
     */
    protected abstract int getLayoutId();

    /**
     * Default implementation of back press
     */
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

    /**
     * Load 1st fragment, used for the template of onCreateView defined in BaseMainActivity
     */
    protected abstract void loadFirstFragment();

    /**
     * Go to a fragment, frequently used in MainActivity
     * @param fragment fragment to go
     * @param bundleInitializer how to pass data to the fragment
     */
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
