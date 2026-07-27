package com.group3.taamapp;

import android.os.Bundle;

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
        String email = model.getCurrentAccount()
        if(email == null) {
            loadFragment(new LoginFragment(), null);
            return;
        }
        loadFragment(new ViewActivty(), new BundleInitializer() {
            public void initBundle(Bundle bundle) {
                bundle.putString("email", email);
            }
        });
    }
}