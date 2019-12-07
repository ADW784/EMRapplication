package com.example.emrapplication.presenters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.model.Caller;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

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




    public void updateCallerProfile(final Caller caller) {
        DatabaseReference userRef = firebaseManager.USERS_DATABASE_REFERENCE.child(caller.uid);

        Map<String, Object> childUpdates = new HashMap<>();
        if(caller.firstName != null && !caller.firstName.isEmpty()) { childUpdates.put("firstName", caller.firstName); }
        if(caller.lastName != null && !caller.lastName.isEmpty()) { childUpdates.put("lastName", caller.lastName); }
        if(caller.allergies != null && !caller.allergies.isEmpty()) { childUpdates.put("allergies", caller.allergies); }
        if(caller.medication != null && !caller.medication.isEmpty()) { childUpdates.put("medication", caller.medication); }
        if(caller.doctor != null && !caller.doctor.isEmpty()) { childUpdates.put("doctor", caller.doctor); }

        userRef.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateCallerProfile:onSuccess: " + caller);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "updateCallerProfile:onFailure: " + e.getMessage());
                    }
                });
    }

}


