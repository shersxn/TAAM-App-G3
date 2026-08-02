package com.group3.taamapp.LogoutPage;

import com.group3.taamapp.Contract.LogoutContract;
import com.group3.taamapp.Model.AuthModel;

public class LogoutPresenter implements LogoutContract.Presenter {
    private final LogoutContract.View view;
    private final AuthModel model;

    public LogoutPresenter(LogoutContract.View view, AuthModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void logout() {
        // Clears the locally stored session (see AuthModelFirebase#signout)
        model.signout();
        view.toastMakeText("You have been logged out.");
        view.toLoginPage();
    }

    @Override
    public void cancel() {
        view.closeLogoutScreen();
    }
}
