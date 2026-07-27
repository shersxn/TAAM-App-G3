package com.group3.taamapp.LoginPage;

import static com.group3.taamapp.Contract.LoginContract.AuthModel.LoginFailure;

import com.group3.taamapp.Contract.LoginContract;
import com.group3.taamapp.Contract.LoginContract.AuthModel;
import com.group3.taamapp.Model.AuthModel.StatusCallback;

/**
 * Implementation of LoginContract.Presenter
 */
public class LoginPresenter implements LoginContract.Presenter {
    /**
     * Used for accessing LoginContract.AuthModel functions
     */
    private final AuthModel model;

    /**
     * Used for accessing LoginContract.View functions
     */
    private final LoginContract.View view;

    /**
     * Constructor of LoginPresenter, inserting view and model to this presenter
     * @param view view inserted
     * @param model model inserted
     */
    public LoginPresenter(LoginContract.View view, AuthModel model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Translate from user input failure code in login to failure message
     * @param failureCode failure code to translate
     * @return translated failure message
     */
    private String loginFailureCodeToMessage(LoginFailure failureCode) {
        if(failureCode == LoginFailure.EMPTY_EMAIL) {
            return "email cannot be empty";
        }
        if(failureCode == LoginFailure.EMPTY_PASSWORD) {
            return "password cannot be empty";
        }
        if(failureCode == LoginFailure.EMAIL_DNE) {
            return "The email you use for login does not exist";
        }
        if(failureCode == LoginFailure.EMAIL_PASSWORD_NOT_MATCH) {
            return "Your email or password are incorrect";
        }
        return null;
    }

    @Override
    public void login() {
        String email = view.getEmail();
        String password = view.getPassword();
        model.login(email, password, new StatusCallback<LoginFailure>() {
            @Override
            public void onSuccess() {
                view.toMainPage(email);
            }

            @Override
            public void onFailure(LoginFailure failureCode) {
                view.toastMakeText("Failure: " + loginFailureCodeToMessage(failureCode));
            }

            @Override
            public void onError(String errorMessage) {
                view.toastMakeText("Error: " + errorMessage);
            }
        });
    }

    @Override
    public void toSignUp() {
        view.toSignUp();
    }
}
