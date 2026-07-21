package com.group3.taamapp;

import android.content.Context;

public interface SignupContract {
    public interface Presenter {
        public void signup();
        public void toLogin();
    }

    public interface View extends BaseViewContract {
        public Context getContext();
        public String getEmail();
        public String getUsername();
        public String getPassword();
        public void toLogin();
    }
}
