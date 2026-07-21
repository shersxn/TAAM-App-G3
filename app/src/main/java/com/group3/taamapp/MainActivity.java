package com.group3.taamapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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