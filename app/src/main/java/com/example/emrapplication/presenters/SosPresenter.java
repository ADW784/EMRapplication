package com.example.emrapplication.presenters;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.model.Caller;
import com.example.emrapplication.model.CustomLocation;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.model.EmergencyStatus;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SosPresenter {

    public interface View {
        void didCreateEmergency(Emergency emergency);
        void errorCreatingEmergency(String message);
        void emergencyExistsForCurrentUser(Emergency emergency);
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

    }


    public void checkIfEmergencyExistsForCurrentUser() {
        firebaseManager.getRefForCurrentUser().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Caller caller = dataSnapshot.getValue(Caller.class);
                if(caller != null) {
                    if(caller.emergency != null && caller.emergency.id != null) {
                        view.emergencyExistsForCurrentUser(caller.emergency);
                        Log.d(TAG, "checkIfEmergencyExistsForCurrentUser:onDataChange: :" + caller.emergency);
                    } else {
                        Log.d(TAG, "checkIfEmergencyExistsForCurrentUser:onDataChange: There was no previous emergency");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "checkIfExistsAndArchiveEmergencyForCurrentUser:onCancelled: " + databaseError.getDetails());
            }
        });
    }

}
