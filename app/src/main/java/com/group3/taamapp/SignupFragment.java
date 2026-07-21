package com.group3.taamapp;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignupFragment extends BaseFragment implements SignupContract.View {
    private SignupContract.Presenter presenter;
    private EditText editTextEmail, editTextUsername, editTextPassword;
    private Button buttonSignup, buttonToLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signup;
    }

    @Override
    protected void setUIComponents(View view) {
        editTextEmail = view.findViewById(R.id.signupEmail);
        editTextUsername = view.findViewById(R.id.signupUsername);
        editTextPassword = view.findViewById(R.id.signupPassword);
        buttonSignup = view.findViewById(R.id.signUpBtn);
        buttonToLogin = view.findViewById(R.id.loginBtnLink);
    }

    @Override
    protected void setEvents() {
        buttonSignup.setOnClickListener(v -> presenter.signup());
        buttonToLogin.setOnClickListener(v -> presenter.toLogin());
    }

    @Override
    protected void setPresenter() {
        presenter = new SignupPresenter(this, new AuthModelFirebase(this.getContext()));
    }

    @Override
    public String getEmail() { return editTextEmail.getText().toString().trim(); }

    @Override
    public String getUsername() { return editTextUsername.getText().toString().trim(); }

    @Override
    public String getPassword() { return editTextPassword.getText().toString().trim(); }

    @Override
    public void toLogin() {
        loadFragment(new LoginFragment(), null);
    }
}
