package com.group3.taamapp;

import com.group3.taamapp.Bases.BaseMainActivity;
import com.group3.taamapp.LoginPage.LoginFragment;
import com.group3.taamapp.Model.AuthModel;
import com.group3.taamapp.Model.AuthModelFirebase;

public class MainActivity extends BaseMainActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void loadFirstFragment() {
        AuthModel model = new AuthModelFirebase(this);
        if(model.getCurrentAccount() == null) {
            loadFragment(new LoginFragment(), null);
            return;
        }
        //TODO: load Main Page
    }
}