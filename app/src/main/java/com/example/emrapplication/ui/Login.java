package com.example.emrapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emrapplication.R;
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

public class Login extends AppCompatActivity {

    private static final String TAG = "Login";

    TextView register;
    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;
    Button guestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.register_textView);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        emailEditText = findViewById(R.id.editTextUserEmail);
        passwordEditText = findViewById(R.id.editTextUserPassword);

        loginButton = findViewById(R.id.buttonLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()) {
                    signInWith(String.valueOf(emailEditText.getText()), String.valueOf(passwordEditText.getText()));
                }
            }
        });

        guestButton = findViewById(R.id.guest_button);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInAsGuest();
            }
        });


        setTitle("Login");

        if(FirebaseManager.getInstance().isLoggedIn()){
            goToSOSActivity();
        }

    }


    private void signInWith(String email, String password) {

        //if (validateInputs()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Log.d(TAG, "onComplete: Successfully signed in with user: " + user);
                        setCurrentUserWithUID(user.getUid());
                        goToSOSActivity();
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(Login.this, "Authentication failed! " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        //}
    }

    private void signInAsGuest() {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    Log.d(TAG, "onComplete: Successfully signed in with anonymous user: " + user);
                    setCurrentUserWithUID(user.getUid());
                    goToSOSActivity();
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Login.this, "Authentication failed! " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private boolean validateInputs() {

        boolean success = true;

        if(String.valueOf(emailEditText.getText()).trim().isEmpty()) {
            success = false;
            emailEditText.setError("Email cannot be empty!");
        }

        if(String.valueOf(passwordEditText.getText()).trim().isEmpty()) {
            success = false;
            passwordEditText.setError("Password cannot be empty!");
        }

        return success;
    }


    private void setCurrentUserWithUID(String uid) {
        FirebaseManager.getInstance().USERS_DATABASE_REFERENCE.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Caller caller = dataSnapshot.getValue(Caller.class);
                FirebaseManager.getInstance().setCurrentUser(caller);
                Log.d(TAG, "onDataChange: current user set to: " + caller.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Unable to set current user! Error:" + databaseError.getMessage());
            }
        });
    }

    private void  goToSOSActivity() {
        Intent intent = new Intent(Login.this, SOS.class);
        startActivity(intent);
    }


}
