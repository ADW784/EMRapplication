package com.example.emrapplication.presenters;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.model.Caller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationPresenter {

    public interface View {
        void successfullyRegisteredUser();
        void errorRegisteringUser(String email, String message);
        void errorAddingUserToDatabase(String email, String message);
    }

    private static final String TAG = "MDB:Reg..Presenter";

    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private View view;

    public RegistrationPresenter(View view) { this.view = view; }

    public void registerNewCaller(final String email, String password, final String firstName, final String lastName) {

        if(firebaseManager.isLoggedInAnonymously()) {

            AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);

            auth.getCurrentUser().linkWithCredential(authCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Log.d(TAG, "registerNewCaller:linkWithCredential:success");
                                //FirebaseUser user = task.getResult().getUser();

                                Caller caller = firebaseManager.getCurrentUser();
                                caller.updateBasicDetails(email,email,firstName,lastName);
                                addNewCallerToDatabase(caller);

                            } else {

                                Log.w(TAG, "registerNewCaller:linkWithCredential:failure", task.getException());
                                view.errorRegisteringUser(email, task.getException().getLocalizedMessage());

                            }

                        }
                    });

        } else {

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        Log.d(TAG, "registerNewCaller:createUserWithEmail:onComplete: Successfully registered new user with email: " + email);
                        Caller caller = new Caller(email, email, uid, firstName, lastName, null, null, null, null);
                        addNewCallerToDatabase(caller);
                    } else {
                        Log.w(TAG, "registerNewCaller:createUserWithEmail:failure", task.getException());
                        view.errorRegisteringUser(email, task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }

    public void addNewCallerToDatabase(final Caller caller) {

        final FirebaseManager firebaseManager = FirebaseManager.getInstance();
        firebaseManager.DATABASE_REFERENCE.child(firebaseManager.USERS_REF).child(caller.uid).setValue(caller).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully added caller to database: " + caller.toString());
                firebaseManager.setCurrentUser(caller);
                view.successfullyRegisteredUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to add caller to database: " + caller.toString() + " error: " + e.getLocalizedMessage());
                view.errorAddingUserToDatabase(caller.email, e.getLocalizedMessage());
            }
        });
    }

}
