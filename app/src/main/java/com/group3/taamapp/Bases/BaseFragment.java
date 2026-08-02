package com.group3.taamapp.Bases;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.group3.taamapp.R;

/**
 * Template Fragment, need to define
 * - getLayoutId()
 * - setUIComponents(View view)
 * - setEvents()
 * - setPresenter()
 */
public abstract class BaseFragment extends EquippedFragment {

    /**
     * Template for onCreateView, defined by
     * - getLayoutId()
     * - setUIComponents(View view)
     * - setEvents()
     * - setPresenter()
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view of this fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        setUIComponents(view);
        setEvents();
        setPresenter();
        return view;
    }

    /**
     * Return the layout id of this fragment, used for the template of onCreateView defined in BaseFragment
     */
    protected abstract int getLayoutId();

    /**
     * Set up UI components, used for the template of onCreateView defined in BaseFragment
     * @param view view used for setting up UI components
     */
    protected abstract void setUIComponents(View view);

    /**
     * Set up events, used for the template of onCreateView defined in BaseFragment
     */
    protected abstract void setEvents();

    /**
     * Make presenter for this view, used for the template of onCreateView defined in BaseFragment
     */
    protected abstract void setPresenter();

    /**
     * Show message using toast, a frequently required function for View Contract
     * @param message message shown
     */
    public void toastMakeText(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
