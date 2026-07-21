package com.group3.taamapp;

import android.content.Context;

public interface LoginContract {
    public static interface Presenter {
        public abstract void login();
        public abstract void toSignUp();
    }

    public static interface View extends BaseViewContract {
        public abstract Context getContext();
        public abstract String getEmail();
        public abstract String getPassword();
        public abstract void toMainPage(String email);
        public abstract void toSignUp();
    }
}
