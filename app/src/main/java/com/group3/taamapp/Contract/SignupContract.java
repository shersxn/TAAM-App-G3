package com.group3.taamapp.Contract;

import com.group3.taamapp.Bases.BaseViewContract;
import com.group3.taamapp.Model.AuthModel.StatusCallback;

/**
 * Defines all functions required for Signup MVP struct
 */
public interface SignupContract {
    /**
     * Defines all functions required for SignupPresenter
     */
    public interface Presenter {
        /**
         * Corresponds to signup button
         */
        public void signup();
        /**
         * Corresponds to toLogin button
         */
        public void toLogin();
    }

    /**
     * Defines all functions required for SignupView
     */
    public interface View extends BaseViewContract {
        /**
         * Return email that the user enters in email EditText
         */
        public String getEmail();

        /**
         * Return username that the user enters in username EditText
         */
        public String getUsername();

        /**
         * Return password that the user enters in password EditText
         */
        public String getPassword();

        /**
         * Go to Login Page
         */
        public void toLogin();
    }

    /**
     * Defines all functions required for SignupAuthModel (including enum for user input failures)
     */
    public interface AuthModel {
        /**
         * Defines all possible failures about user inputs when signing up
         */
        public static enum SignUpFailure {
            EMPTY_EMAIL,
            EMPTY_USERNAME,
            EMPTY_PASSWORD,
            INVALID_EMAIL,
            EMAIL_EXISTS;
        }

        /**
         * Try signing up in using 'email', 'username', 'password', and do correspond actions defined in 'signUpCallback'
         * @param email email used in signup
         * @param username username used in signup
         * @param password password used in signup
         * @param signUpCallback defines what to do when success(onSuccess), meet failure caused by user input(onFailure) and other failures (onError)
         */
        public abstract void signUp(String email, String username, String password, StatusCallback<SignUpFailure> signUpCallback);
    }
}
