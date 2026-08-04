package com.group3.taamapp.LogoutPage;

import android.view.View;

import com.group3.taamapp.Bases.BaseFragment;
import com.group3.taamapp.Contract.LogoutContract;
import com.group3.taamapp.LoginPage.LoginFragment;
import com.group3.taamapp.Model.AuthModelFirebase;
import com.group3.taamapp.R;
import com.google.android.material.button.MaterialButton;

/**
 * Logout screen that allows the user to log out or cancel.
 */
public class LogoutFragment extends BaseFragment implements LogoutContract.View {

    // Presenter handles the logout business logic
    private LogoutContract.Presenter presenter;

    // Logout screen buttons
    private MaterialButton buttonLogout;
    private MaterialButton buttonCancel;

    @Override
    protected int getLayoutId() {
        return R.layout.logout;
    }

    @Override
    protected void setUIComponents(View view) {
        // Connect UI components to their XML
        buttonLogout = view.findViewById(R.id.btn_logout);
        buttonCancel = view.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void setEvents() {
        // Set button click listeners
        buttonLogout.setOnClickListener(v -> presenter.logout());
        buttonCancel.setOnClickListener(v -> presenter.cancel());
    }

    @Override
    protected void setPresenter() {
        // Create the presenter and authentication model
        presenter = new LogoutPresenter(this, new AuthModelFirebase(getContext()));
    }

    @Override
    public void toLoginPage() {
        // Navigate to the login screen after a successful logout
        loadFragment(new LoginFragment(), null);
    }

    @Override
    public void closeLogoutScreen() {
        // Return to the previous screen when Cancel is pressed
        getParentFragmentManager().popBackStack();
    }
}