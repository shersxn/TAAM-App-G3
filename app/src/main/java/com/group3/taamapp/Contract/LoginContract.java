package com.group3.taamapp.Contract;

import android.content.Context;

import com.group3.taamapp.Bases.BaseViewContract;

public interface LoginContract {
    public static interface Presenter {
        public abstract void login();
        public abstract void toSignUp();
    }

    public static interface View extends BaseViewContract {
        public abstract String getEmail();
        public abstract String getPassword();
        public abstract void toMainPage(String email);
        public abstract void toSignUp();
    }
}
