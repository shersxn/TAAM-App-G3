package com.group3.taamapp;

public interface AuthModel {
    public static interface StatusCallback<F extends Enum<F>> {
        public abstract void onSuccess();

        public abstract void onFailure(F failureCode);

        public abstract void onError(String errorMessage);
    }

    public static enum LoginFailure {
        EMPTY_EMAIL,
        EMPTY_PASSWORD,
        EMAIL_DNE,
        EMAIL_PASSWORD_NOT_MATCH;
    }
    public abstract void login(String email, String password, StatusCallback<LoginFailure> loginCallback);

    public static enum SignUpFailure {
        EMPTY_EMAIL,
        EMPTY_USERNAME,
        EMPTY_PASSWORD,
        INVALID_EMAIL,
        EMAIL_EXISTS;
    }
    public abstract void signUp(String email, String username, String password, StatusCallback<SignUpFailure> signUpCallback);

    public abstract String getCurrentAccount();

    public abstract void signout();
}
