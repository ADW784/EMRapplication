package com.example.emrapplication.managers;

import android.util.Log;

import com.example.emrapplication.model.Caller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    private static final String TAG = "MDB:FirebaseManager";

    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private Caller currentUser = null;

    public final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();
    public final String USERS_REF = "users";
    public final DatabaseReference USERS_DATABASE_REFERENCE = DATABASE_REFERENCE.child(USERS_REF);

    private FirebaseManager(){
        auth = FirebaseAuth.getInstance();
    };

    public static FirebaseManager getInstance() {
        if(instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public Caller getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Caller currentUser) {
        this.currentUser = currentUser;
    }


    public boolean isLoggedInAnonymously() {

        if (isLoggedIn()) {
            if (currentUser != null) {
                if(currentUser.email == null) {
                    return true;
                }
            } else {
                Log.d(TAG, "isLoggedInAnonymously: user logged in but not set. Check App Logic!!");
            }
        }

        return false;
    }

}
