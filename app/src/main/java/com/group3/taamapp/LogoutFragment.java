package com.group3.taamapp.LogoutPage;

import android.view.View;
import android.widget.Button;

import com.group3.taamapp.Bases.BaseFragment;
import com.group3.taamapp.Contract.LogoutContract;
import com.group3.taamapp.LoginPage.LoginFragment;
import com.group3.taamapp.Model.AuthModelFirebase;
import com.group3.taamapp.R;

/**
 * Implementation of LogoutContract.View, as a Fragment (was previously LogoutActivity.java, which now is LogoutPresenter).
 */
public class LogoutFragment extends BaseFragment implements LogoutContract.View {

    private LogoutContract.Presenter presenter;

    private Button buttonLogout, buttonCancel;

    @Override
    protected int getLayoutId() {
        return R.layout.logout;
    }

    @Override
    protected void setUIComponents(View view) {
        buttonLogout = view.findViewById(R.id.btn_logout);
        buttonCancel = view.findViewById(R.id.btn_cancel);
    }

    @Override
    protected void setEvents() {
        buttonLogout.setOnClickListener(v -> presenter.logout());
        buttonCancel.setOnClickListener(v -> presenter.cancel());
    }

    @Override
    protected void setPresenter() {
        presenter = new LogoutPresenter(this, new AuthModelFirebase(getContext()));
    }

    @Override
    public void toLoginPage() {
        // Go to another fragment, using EquippedFragment's loadFragment()
        loadFragment(new LoginFragment(), null);
    }

    @Override
    public void closeLogoutScreen() {
        // "Cancel" means returning to whichever fragment sent the user here, rather than navigating to a specific new one
        getParentFragmentManager().popBackStack();
    }
}