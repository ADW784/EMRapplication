package com.example.emrapplication.presenters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.model.Caller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserInfoPresenter {

    private static final String TAG = "MDB:UserInfoPresenter";

    public interface View {
        void onUserChanged(Caller caller);
    }

    View view;

    FirebaseManager firebaseManager = FirebaseManager.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public UserInfoPresenter(View view) { this.view = view; }

    public void getCurrentUser() {

        if(firebaseManager.isLoggedIn()) {

            firebaseManager.USERS_DATABASE_REFERENCE.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        Caller caller = dataSnapshot.getValue(Caller.class);
                        if(caller != null) {
                            firebaseManager.setCurrentUser(caller);
                            view.onUserChanged(caller);
                            Log.d(TAG, "getCurrentUser:onDataChange: current user set to: " + caller.toString());
                        } else {
                            Log.d(TAG, "getCurrentUser:onDataChange: could not get caller from snapshot: " + dataSnapshot.toString());
                        }
                    } else {
                        Log.d(TAG, "getCurrentUser:onDataChange: snapshot does not exist!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "getCurrentUser:onCancelled: Unable to get current user! Error:" + databaseError.getMessage());
                }
            });

        }

    }
}


