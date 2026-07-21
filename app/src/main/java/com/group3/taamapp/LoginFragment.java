package com.group3.taamapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginFragment extends BaseFragment implements LoginContract.View {
    private LoginContract.Presenter presenter;

    private EditText editTextEmail, editTextPassword;

    private Button buttonSignIn, buttonSignUp;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected void setUIComponents(View view) {
        editTextEmail = view.findViewById(R.id.loginEmail);
        editTextPassword = view.findViewById(R.id.loginPassword);
        buttonSignIn = view.findViewById(R.id.signInBtn);
        buttonSignUp = view.findViewById(R.id.signUpBtnLink);
    }

    @Override
    protected void setEvents() {
        buttonSignIn.setOnClickListener(v -> presenter.login());
        buttonSignUp.setOnClickListener(v -> presenter.toSignUp());
    }

    @Override
    protected void setPresenter() {
        presenter = new LoginPresenter(this, new AuthModelFirebase(this.getContext()));
    }

    @Override
    public String getEmail() {
        return editTextEmail.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return editTextPassword.getText().toString().trim();
    }

    @Override
    public void toMainPage(String email) {
        loadFragment(
        //TODO: new MainFragment(),
        new BundleInitializer() {
            @Override
            public void initBundle(Bundle bundle) {
                bundle.putString("email", email);
            }
        });
    }

    @Override
    public void toSignUp() {
        loadFragment(new SignupFragment(), null);
    }
}
