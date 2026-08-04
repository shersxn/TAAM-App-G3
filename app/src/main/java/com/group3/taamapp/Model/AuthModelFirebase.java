package com.group3.taamapp.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthModel implementation using Firebase(cloud) & SharedPreferences(local)
 */
public class AuthModelFirebase implements AuthModel{
    /**
     * Reference used for accessing firebase
     */
    private final DatabaseReference rootRef;

    /**
     * Reference used for accessing local storage
     */
    private final SharedPreferences sharedPref;

    /**
     * encode email in a Firebase friendly way, to solve the problem that firebase cannot contain .
     * @param email email to encode
     * @return return encoded email
     */
    private String encodeEmail(String email) {
        return email.replace('.', ',');
    }

    /**
     * Constructor of AuthModelFirebase
     * @param context context required for SharedPreferences reference
     */
    public AuthModelFirebase(Context context) {
        rootRef = FirebaseDatabase.getInstance("https://cscb07-group3-taamapp-default-rtdb.firebaseio.com/").getReference();
        sharedPref = context.getSharedPreferences("Session", Context.MODE_PRIVATE);
    }

    @Override
    public void login(String email, String password, StatusCallback<LoginFailure> loginCallback) {
        if(email == null) {
            throw new NullPointerException("AuthModelFirebase.login: email cannot be null");
        }
        if(password == null) {
            throw new NullPointerException("AuthModelFirebase.login: password cannot be null");
        }
        if(loginCallback == null) {
            throw new NullPointerException("AuthModelFirebase.login: loginCallback cannot be null");
        }
        if(email.isEmpty()) {
            loginCallback.onFailure(LoginFailure.EMPTY_EMAIL);
            return;
        }
        if(password.isEmpty()) {
            loginCallback.onFailure(LoginFailure.EMPTY_PASSWORD);
            return;
        }
        rootRef.child("Auth").child(encodeEmail(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    loginCallback.onFailure(LoginFailure.EMAIL_DNE);
                    return;
                }
                if(!password.equals(snapshot.getValue())){
                    loginCallback.onFailure(LoginFailure.EMAIL_PASSWORD_NOT_MATCH);
                    return;
                }
                // store email to local
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("email", encodeEmail(email));
                editor.commit();
                loginCallback.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loginCallback.onError("Firebase connection cancelled");
            }
        });
    }

    @Override
    public void signUp(String email, String username, String password, StatusCallback<SignUpFailure> signUpCallback) {
        if(email == null) {
            throw new NullPointerException("AuthModelFirebase.signUp: email cannot be null");
        }
        if(username == null) {
            throw new NullPointerException("AuthModelFirebase.signUp: username cannot be null");
        }
        if(password == null) {
            throw new NullPointerException("AuthModelFirebase.signUp: password cannot be null");
        }
        if(signUpCallback == null) {
            throw new NullPointerException("AuthModelFirebase.signUp: signUpCallback cannot be null");
        }
        if(email.isEmpty()) {
            signUpCallback.onFailure(SignUpFailure.EMPTY_EMAIL);
            return;
        }
        if(username.isEmpty()) {
            signUpCallback.onFailure(SignUpFailure.EMPTY_USERNAME);
            return;
        }
        if(password.isEmpty()) {
            signUpCallback.onFailure(SignUpFailure.EMPTY_PASSWORD);
            return;
        }
        rootRef.child("Auth").child(encodeEmail(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    signUpCallback.onFailure(SignUpFailure.EMAIL_EXISTS);
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    signUpCallback.onFailure(SignUpFailure.INVALID_EMAIL);
                    return;
                }
                Map<String, Object> updates = new HashMap<>();
                // update in Auth
                updates.put("/Auth/"+encodeEmail(email), password);
                // update in Users
                User user = new User(username, new HashMap<>(), false);
                updates.put("/Users/"+encodeEmail(email), user);
                rootRef.updateChildren(updates).addOnSuccessListener(
                        aVoid -> signUpCallback.onSuccess()
                ).addOnFailureListener(
                        e -> signUpCallback.onError(e.getMessage())
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                signUpCallback.onError("Firebase connection cancelled");
            }
        });
    }

    @Override
    public String getCurrentAccount() {
        return sharedPref.getString("email", null);
    }

    @Override
    public void signout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("email");
        editor.commit();
    }
}
