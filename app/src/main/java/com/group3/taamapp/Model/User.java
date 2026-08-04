package com.group3.taamapp.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User model in Firebase
 */
public class User {
    private String username;


    /**
     * Lot number of the collections of the user
     */
    private Map<String, Boolean> collections;

    /**
     * If this user have admin permission
     */
    private boolean admin;

    public User() {
        collections = new HashMap<>();
    }

    public User(String username, Map<String, Boolean> collections, boolean admin) {
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

    public  Map<String, Boolean> getCollections() {
        return collections;
    }

    public void setCollections(Map<String, Boolean> collections) {
        this.collections = collections;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
