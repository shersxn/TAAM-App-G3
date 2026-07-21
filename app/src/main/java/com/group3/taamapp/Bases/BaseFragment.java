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

public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        setUIComponents(view);
        setEvents();
        setPresenter();
        return view;
    }

    protected abstract int getLayoutId();

    protected abstract void setUIComponents(View view);

    protected abstract void setEvents();

    protected abstract void setPresenter();

    public void toastMakeText(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

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
}
