package com.group3.taamapp.Contract;

import com.group3.taamapp.Bases.BaseViewContract;
import com.group3.taamapp.Model.AuthModel.StatusCallback;

/**
 * Defines all functions required for Login MVP struct
 */
public interface LoginContract {
    /**
     * Defines all functions required for LoginPresenter
     */
    public static interface Presenter {
        /**
         * Corresponds to login button
         */
        public abstract void login();

        /**
         * Corresponds to toSignUp button
         */
        public abstract void toSignUp();
    }

    /**
     * Defines all functions required for LoginView
     */
    public static interface View extends BaseViewContract {
        /**
         * Return email that the user enters in email EditText
         */
        public abstract String getEmail();

        /**
         * Return password that the user enters in password EditText
         */
        public abstract String getPassword();

        /**
         * Go to Main Page
         * @param email logged in email
         */
        public abstract void toMainPage(String email);

        /**
         * Go to Sign Up Page
         */
        public abstract void toSignUp();
    }

    /**
     * Defines all functions required for LoginAuthModel (including enum for user input failures)
     */
    public static interface AuthModel {
        /**
         * Defines all possible failures about user inputs when logging in
         */
        public static enum LoginFailure {
            EMPTY_EMAIL,
            EMPTY_PASSWORD,
            EMAIL_DNE,
            EMAIL_PASSWORD_NOT_MATCH;
        }

        /**
         * Try logging in using 'email', 'password', and do correspond actions defined in 'loginCallback'
         * @param email email used in login
         * @param password password used in login
         * @param loginCallback defines what to do when success(onSuccess), meet failure caused by user input(onFailure) and other failures (onError)
         */
        public abstract void login(String email, String password, StatusCallback<LoginFailure> loginCallback);
    }
}
