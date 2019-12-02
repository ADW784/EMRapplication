package com.example.emrapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    Button buttonRegister;
    TextView textViewLoginLink;

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    EditText confirmPassword;

    private static final String TAG = "Registration";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        textViewLoginLink = findViewById(R.id.textViewLoginLink);
        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //default code. simply takes you to the next activity.
                Intent i = new Intent(Registration.this,Login.class);
                startActivity(i);
            }
        });

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateInputs()) {
                    registerNewCaller(String.valueOf(email.getText()), String.valueOf(password.getText()), String.valueOf(firstName.getText()), String.valueOf(lastName.getText()));
                } else {
                    Toast.makeText(Registration.this, R.string.registration_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.editTextConfirmPassword);

        setTitle("Registration");

    }


    private boolean validateInputs() {

        boolean success = true;

        if(String.valueOf(firstName.getText()).trim().isEmpty()) {
            success = false;
            firstName.setError("First name cannot be empty!");
        }

        if(String.valueOf(lastName.getText()).trim().isEmpty()) {
            success = false;
            lastName.setError("Last name cannot be empty!");
        }

        if(String.valueOf(email.getText()).trim().isEmpty()) {
            success = false;
            email.setError("Email cannot be empty!");
        }

        if(String.valueOf(password.getText()).trim().length()<6) {
            success = false;
            password.setError("Password must be at least 6 characters long");
        } else if(!(String.valueOf(password.getText()).equals(String.valueOf(confirmPassword.getText())))) {
            success = false;
            password.setError("passwords do not match!");
            confirmPassword.setError("passwords do not match!");
        }

        return success;
    }

    public void registerNewCaller(final String email, String password, final String firstName, final String lastName) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();
                    Log.d(TAG, "onComplete: Successfully registered new user with email: " + email);
                    Caller caller = new Caller(email, email, uid, firstName, lastName, null, null);
                    addNewCallerToDatabase(caller);
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addNewCallerToDatabase(final Caller caller) {

        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        firebaseManager.DATABASE_REFERENCE.child(firebaseManager.USERS_REF).child(caller.uid).setValue(caller).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully added caller to database: " + caller.toString());
                Intent intent = new Intent(Registration.this, SOS.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to add caller to database: " + caller.toString());
                Toast.makeText(Registration.this, "Error adding user to database", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
