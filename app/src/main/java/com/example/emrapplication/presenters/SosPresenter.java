package com.example.emrapplication.presenters;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.model.CustomLocation;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.model.EmergencyStatus;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SosPresenter {

    public interface View {
        void didCreateEmergency(Emergency emergency);
        void errorCreatingEmergency(String message);
    }

    private static final String TAG = "MDB:SosPresenter";

    private View view;
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public SosPresenter(View view) { this.view = view; }

    public void createNewEmergency(Location location, String description) {

        Date date = new Date();

        String emergencyId = firebaseManager.ACTIVE_EMERGENCIES_DATABASE_REFERENCE.push().getKey();

        String currentUid = auth.getCurrentUser().getUid();

        CustomLocation customLocation = new CustomLocation(location);

        final Emergency emergency = new Emergency(emergencyId, date.getTime(), currentUid, null, description, EmergencyStatus.CREATED, customLocation);

        Map<String, Object> emergencyMapped = emergency.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(firebaseManager.ACTIVE_EMERGENCIES_REF + "/" + emergencyId, emergencyMapped);
        childUpdates.put(firebaseManager.USERS_REF + "/" + currentUid + "/" + firebaseManager.EMERGENCY_REF, emergencyMapped);

        firebaseManager.DATABASE_REFERENCE.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "createNewEmergency:onSuccess: ");
                        view.didCreateEmergency(emergency);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "createNewEmergency:onFailure: " + e.getMessage());
                        view.errorCreatingEmergency(e.getLocalizedMessage());
                    }
                });

//        firebaseManager.DATABASE_REFERENCE.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
//            @Override
//            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                Log.d(TAG, "createNewEmergency:onComplete: database reference: " + databaseReference);
//                if(databaseError != null) {
//                    Log.d(TAG, "createNewEmergency:onComplete: error creating emergencies: " + databaseError.getMessage());
//                    view.errorCreatingEmergency(databaseError.getMessage());
//                } else {
//                    view.didCreateEmergency(emergency);
//                }
//            }
//        });

    }

}
