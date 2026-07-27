package com.group3.taamapp.Model;

import com.group3.taamapp.Contract.LoginContract;
import com.group3.taamapp.Contract.SignupContract;

/**
 * All required functions for AuthModel, including
 * - All required function in Contract
 * - Required function in MainActivity (getAccount)
 * - Required function in Main Page (signout)
 */
public interface AuthModel extends LoginContract.AuthModel, SignupContract.AuthModel {
    /**
     * Callback with only if it success, defines what to do when
     * success(onSuccess), meet failure caused by user input(onFailure) and other failures (onError)
     * @param <F> enum for user input failures
     */
    public static interface StatusCallback<F extends Enum<F>> {
        /**
         * What to do when success
         */
        public abstract void onSuccess();

        /**
         * What to do when meet user input failures
         * @param failureCode The specific user input failure meet
         */
        public abstract void onFailure(F failureCode);

        /**
         * What to do when meet other failures
         * @param errorMessage Error message about the failure
         */
        public abstract void onError(String errorMessage);
    }

    /**
     * Get current logged in account
     * @return email of the logged in account if logged in. Otherwise, return null
     */
    public abstract String getCurrentAccount();

    /**
     * Remove login status
     */
    public abstract void signout();
}
