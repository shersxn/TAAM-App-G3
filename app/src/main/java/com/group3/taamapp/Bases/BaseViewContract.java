package com.group3.taamapp.Bases;

/**
 * Defines the functions required for all View contract
 */
public interface BaseViewContract {
    /**
     * Show message using toast, a frequently required function for View Contract
     * @param message message shown
     */
    public abstract void toastMakeText(String message);
}
