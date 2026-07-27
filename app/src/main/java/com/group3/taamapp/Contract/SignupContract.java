package com.group3.taamapp.Contract;

import android.content.Context;

import com.group3.taamapp.Bases.BaseViewContract;

public interface SignupContract {
    public interface Presenter {
        public void signup();
        public void toLogin();
    }

    public interface View extends BaseViewContract {
        public String getEmail();
        public String getUsername();
        public String getPassword();
        public void toLogin();
    }
}
