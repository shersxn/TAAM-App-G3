package com.group3.taamapp;
import static com.group3.taamapp.AuthModel.*;

public class SignupPresenter implements SignupContract.Presenter {
    AuthModel model;
    SignupContract.View view;

    public SignupPresenter(SignupContract.View view, AuthModel model) {
        this.view = view;
        this.model = model;
    }

    private String signupFailureCodeToMessage(SignUpFailure failureCode) {
        if(failureCode == SignUpFailure.EMPTY_EMAIL) {
            return "email cannot be empty";
        }
        if(failureCode == SignUpFailure.EMPTY_USERNAME) {
            return "username cannot be empty";
        }
        if(failureCode == SignUpFailure.EMPTY_PASSWORD) {
            return "password cannot be empty";
        }
        if(failureCode == SignUpFailure.INVALID_EMAIL) {
            return "This is not a valid email";
        }
        if(failureCode == SignUpFailure.EMAIL_EXISTS) {
            return "The email you use for login has existed";
        }
        return null;
    }

    @Override
    public void signup() {
        String email = view.getEmail();
        String username = view.getUsername();
        String password = view.getPassword();
        model.signUp(email, username, password, new StatusCallback<SignUpFailure>() {
            @Override
            public void onSuccess() {
                view.toastMakeText("You have successfully signed up an account.");
            }

            @Override
            public void onFailure(SignUpFailure failureCode) {
                view.toastMakeText("Failure: " + signupFailureCodeToMessage(failureCode));
            }

            @Override
            public void onError(String errorMessage) {
                view.toastMakeText("Error: " + errorMessage);
            }
        });
    }

    @Override
    public void toLogin() { view.toLogin(); }
}
