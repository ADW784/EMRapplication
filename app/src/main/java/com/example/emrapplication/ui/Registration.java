package com.example.emrapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emrapplication.R;

public class Registration extends AppCompatActivity {

    Button buttonRegister;
    TextView textViewLoginLink;

    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;
    EditText confirmPassword;


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
                    //default code. simply takes you to the next activity.
                    Intent i = new Intent(Registration.this,SOS.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(), R.string.registration_error_message, Toast.LENGTH_SHORT).show();
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
            password.setError("Password cannot be empty!");
        } else if(!password.equals(confirmPassword)) {
            success = false;
            password.setError("passwords do not match!");
            confirmPassword.setError("passwords do not match!");
        }

        return success;
    }
}
