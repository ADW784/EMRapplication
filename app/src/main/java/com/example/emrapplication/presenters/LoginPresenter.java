package com.example.emrapplication.presenters;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.model.Caller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginPresenter {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseManager firebaseManager = FirebaseManager.getInstance();

    public interface View {
        void onSuccessfulSignIn();
        void onSignInError(String email, String message);
        void onAnonymousSignInError(String message);
        void onSuccessfulGuestSignIn();
    }

    private static final String TAG = "MDB:LoginPresenter";

    private View view;

    public LoginPresenter(View view) { this.view = view; }


    public void signInWith(final String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    Log.d(TAG, "signInWith:onComplete: Successfully signed in with user: " + user);
                    setCurrentUserWithUID(user.getUid());
                    view.onSuccessfulSignIn();
                } else {
                    Log.w(TAG, "signInWith:createUserWithEmail:failure", task.getException());
                    view.onSignInError(email, task.getException().getLocalizedMessage());
                }
            }
        });
    }

    public void signInAsGuest() {
        if(auth.getCurrentUser() != null) {
            if(auth.getCurrentUser().isAnonymous()){
                view.onSuccessfulGuestSignIn();
            } else {
                auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Log.d(TAG, "signInAsGuest:onComplete: Successfully signed in with anonymous user: " + user);
                            createUserFromAnonymousSignIn(user);
                            view.onSuccessfulGuestSignIn();
                        } else {
                            Log.w(TAG, "signInAsGuest:createUserWithEmail:failure", task.getException());
                            view.onAnonymousSignInError(task.getException().getLocalizedMessage());
                        }
                    }
                });
            }
        } else {
            auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Log.d(TAG, "signInAsGuest:onComplete: Successfully signed in with anonymous user: " + user);
                        createUserFromAnonymousSignIn(user);
                        view.onSuccessfulGuestSignIn();
                    } else {
                        Log.w(TAG, "signInAsGuest:createUserWithEmail:failure", task.getException());
                        view.onAnonymousSignInError(task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }

    private void createUserFromAnonymousSignIn(final FirebaseUser user) {
        final Caller caller = new Caller(user.getEmail(), user.getEmail(), user.getUid(), null, null, null,null, null, null);
        FirebaseManager.getInstance().USERS_DATABASE_REFERENCE.child(user.getUid()).setValue(caller).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    if(caller != null) {
                        setCurrentUserWithUID(user.getUid());
                        Log.d(TAG, "createUserFromAnonymousSignIn:onComplete: Successfully created anonymous user: " + caller);
                    } else {
                        Log.d(TAG, "createUserFromAnonymousSignIn:onComplete: created anonymous user but caller is null!!");
                    }

                } else {
                    Log.d(TAG, "createUserFromAnonymousSignIn:onComplete: Could not set database user from anonymous user with uid: " + user.getUid());

                }
            }
        });
    }

    public void setCurrentUserWithUID(String uid) {

        FirebaseManager.getInstance().USERS_DATABASE_REFERENCE.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Caller caller = dataSnapshot.getValue(Caller.class);
                    if(caller != null) {
                        FirebaseManager.getInstance().setCurrentUser(caller);
                        Log.d(TAG, "setCurrentUserWithUID:onDataChange: current user set to: " + caller.toString());
                    } else {
                        Log.d(TAG, "setCurrentUserWithUID:onDataChange: could not get caller from snapshot: " + dataSnapshot.toString());
                    }
                } else {
                    Log.d(TAG, "setCurrentUserWithUID:onDataChange: snapshot does not exist!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "setCurrentUserWithUID:onCancelled: Unable to set current user! Error:" + databaseError.getMessage());
            }
        });
    }

}
