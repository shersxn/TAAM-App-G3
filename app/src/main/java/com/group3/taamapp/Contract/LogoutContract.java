package com.group3.taamapp.Contract;

import com.group3.taamapp.Bases.BaseViewContract;

public interface LogoutContract {

    public static interface Presenter {
        // Clears the current session and signals the view to navigate away
        public abstract void logout();

        // User backed out of the logout confirmation
        public abstract void cancel();
    }

    public static interface View extends BaseViewContract {
        // Navigate back to the login/sign-up screen after a successful logout
        public abstract void toLoginPage();

        // Dismiss/close the logout confirmation screen without logging out
        public abstract void closeLogoutScreen();
    }
}
