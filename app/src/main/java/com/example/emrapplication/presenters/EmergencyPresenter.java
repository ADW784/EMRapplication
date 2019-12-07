package com.example.emrapplication.presenters;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.model.CustomLocation;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.model.EmergencyStatus;
import com.example.emrapplication.model.Responder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class EmergencyPresenter {


    public interface View {
        void didRetrieveEmergencyDetails(Emergency emergency);
        void didArchiveEmergency(Emergency emergency);
        //void didUpdateResponder(Responder responder);
        void didRemoveEmergencyFromActiveList(Emergency emergency);
        void didRemoveEmergencyFromUser(Emergency emergency);
        void errorRemovingEmergencyFromUser(String message);
        void errorRetrievingEmergencyDetails(String message);
        void errorArchivingEmergency(String message);
        void errorRemovingEmergencyFromActiveList(String message);
    }

    private static final String TAG = "MDB:EmergencyPresenter";

    private View view;

    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    //private DatabaseReference currentCaller;

    private ValueEventListener userEmergencyListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            Log.d(TAG, "userEmergencyListener:onDataChange: snapshot: " + dataSnapshot);
//            Log.d(TAG, "userEmergencyListener:onDataChange: snapshot exists: " + dataSnapshot.exists());
//            Log.d(TAG, "userEmergencyListener:onDataChange: result" + dataSnapshot.getValue());
            Emergency emergency = dataSnapshot.getValue(Emergency.class);
            Log.d(TAG, "userEmergencyListener:onDataChange: emergency: " + emergency);
            if(emergency != null && emergency.id != null) {
                view.didRetrieveEmergencyDetails(emergency);

                if(emergency.status.equals(EmergencyStatus.INPROGRESS.toString())){
                    removeEmergencyFromActiveList(emergency);
                } else if(emergency.status.equals(EmergencyStatus.CANCELLED.toString()) || emergency.status.equals(EmergencyStatus.RESOLVED.toString())){
                    archiveEmergency(emergency);
                    removeEmergencyFromActiveList(emergency);
                    removeEmergencyFromUser(emergency);
                }
            } else {
                Log.d(TAG, "userEmergencyListener:onDataChange: emergency is null, check programming logic!");
            }

        }

//        private ValueEventListener responderListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "userEmergencyListener:onCancelled: error retrieving emergency: " + databaseError.getMessage());
            view.errorRetrievingEmergencyDetails(databaseError.getMessage());
        }
    };

    public EmergencyPresenter(View view) { this.view = view; }

    public void getEmergencyDetailsForCurrentUser() {
        firebaseManager.getRefForCurrentUser().child(firebaseManager.EMERGENCY_REF).addValueEventListener(userEmergencyListener);
    }

    public void upDateEmergencyStatusForCurrentUser(final EmergencyStatus status){
        firebaseManager.getRefForCurrentUser().child(firebaseManager.EMERGENCY_REF).child("status")
                .setValue(status.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "upDateEmergencyStatusForCurrentUser:onSuccess: status: " + status.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "upDateEmergencyStatusForCurrentUser:onFailure: error: " + e.getMessage());
                    }
                });
    }

    public void removeEmergencyFromActiveList(final Emergency emergency) {


        firebaseManager.ACTIVE_EMERGENCIES_DATABASE_REFERENCE.child(emergency.id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "removeEmergencyFromActiveList:onComplete: successfully removed emergency from active list. emergency: " + emergency);
                view.didRemoveEmergencyFromActiveList(emergency);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "removeEmergencyFromActiveList:onComplete: error:" + e.getMessage());
                view.errorRemovingEmergencyFromActiveList(e.getLocalizedMessage());
            }
        });

    }

    public void archiveEmergency(final Emergency emergency) {

        firebaseManager.EMERGENCY_HISTORY_DATABASE_REFERENCE.child(emergency.id).setValue(emergency).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "archiveEmergency:onSuccess: successfully archived emergency.");
                view.didArchiveEmergency(emergency);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "archiveEmergency:onComplete: error:" + e.getMessage());
                view.errorArchivingEmergency(e.getLocalizedMessage());
            }
        });

    }

    public void removeEmergencyFromUser(final Emergency emergency) {
        firebaseManager.USERS_DATABASE_REFERENCE.child(emergency.callerId).child(firebaseManager.EMERGENCY_REF).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "removeEmergencyFromUser:onSuccess: removed emergency from user");
                        view.didRemoveEmergencyFromUser(emergency);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "removeEmergencyFromUser:onFailure: error:" + e.getMessage());
                view.errorRemovingEmergencyFromUser(e.getLocalizedMessage());
            }
        });
    }

    public void updateEmergencyLocation(Location location){

        firebaseManager.getRefForCurrentUser().child(firebaseManager.EMERGENCY_REF).child("location").setValue(new CustomLocation(location))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateEmergencyLocation:onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "updateEmergencyLocation:onFailure: " + e.getMessage());
            }
        });
    }



    public void updateResponder(Responder responder){
        firebaseManager.getRefForCurrentUser().child(firebaseManager.EMERGENCY_REF).child("responder").setValue(responder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateResponder:onSuccess: ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "updateResponder:onFailure: " + e.getMessage());
            }
        });
    }

    public void testResponse() {
        CustomLocation location = new CustomLocation("test");
        location.setLatitude(13);
        location.setLongitude(-59);

        Responder responder = new Responder("Tom", "Keen", location);

        updateResponder(responder);

        upDateEmergencyStatusForCurrentUser(EmergencyStatus.INPROGRESS);
    }

}
