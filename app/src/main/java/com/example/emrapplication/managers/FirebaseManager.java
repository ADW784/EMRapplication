package com.example.emrapplication.managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.emrapplication.model.Caller;
import com.example.emrapplication.ui.Registration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Struct;
import java.util.Set;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";

    private static FirebaseManager instance;
    private FirebaseAuth auth;

    public final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();
    public final String USERS_REF = "users";

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





}
