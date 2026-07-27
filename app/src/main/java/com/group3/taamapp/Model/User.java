package com.group3.taamapp.Model;

import java.util.ArrayList;

/**
 * User model in Firebase
 */
public class User {
    private String username;

    /**
     * Lot number of the collections of the user
     */
    private ArrayList<String> collections;

    /**
     * If this user have admin permission
     */
    private boolean admin;

    public User() {}

    public User(String username, ArrayList<String> collections, boolean admin) {
        this.username = username;
        this.collections = collections;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getCollections() {
        return collections;
    }

    public void setCollections(ArrayList<String> collections) {
        this.collections = collections;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
