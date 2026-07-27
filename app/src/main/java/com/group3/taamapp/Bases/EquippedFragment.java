package com.group3.taamapp.Bases;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.group3.taamapp.R;

/**
 * Fragment equipped with 2 useful functions
 * - loadFragment: used for going to other pages
 * - loadSubFragment: used for loading sub fragment
 */
public abstract class EquippedFragment extends Fragment{
    /**
     * Go to another fragment, a frequently used function when implementing View contract functions
     * @param fragment instance of the fragment to go, usually is Fragment()
     * @param bundleInitializer defines how to pass the data to the fragment, null if no passing data
     */
    protected void loadFragment(Fragment fragment, BundleInitializer bundleInitializer) {
        if(bundleInitializer != null) {
            Bundle bundle = new Bundle();
            bundleInitializer.initBundle(bundle);
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Load subFragment, a frequently used function when implementing View contract functions
     * @param fragmentLayoutId id of the sub fragment layout
     * @param fragment instance of the sub fragment, usually is Fragment()
     * @param bundleInitializer defines how to pass the data to the sub fragment, null if no passing data
     */
    protected void loadSubFragment(int fragmentLayoutId, Fragment fragment, BundleInitializer bundleInitializer) {
        if(bundleInitializer != null) {
            Bundle bundle = new Bundle();
            bundleInitializer.initBundle(bundle);
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(fragmentLayoutId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
