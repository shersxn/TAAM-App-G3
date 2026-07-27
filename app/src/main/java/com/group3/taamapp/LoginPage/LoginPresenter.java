package com.group3.taamapp.LoginPage;

import static com.group3.taamapp.Model.AuthModel.*;

import com.group3.taamapp.Contract.LoginContract;
import com.group3.taamapp.Model.AuthModel;

public class LoginPresenter implements LoginContract.Presenter {
    private final AuthModel model;
    private final LoginContract.View view;

    public LoginPresenter(LoginContract.View view, AuthModel model) {
        this.view = view;
        this.model = model;
    }

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
                view.toastMakeText("Error: "+ errorMessage);
            }
        });
    }

    @Override
    public void toSignUp() {
        view.toSignUp();
    }
}
