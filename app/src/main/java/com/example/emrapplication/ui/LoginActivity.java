package com.example.emrapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.emrapplication.R;
import com.example.emrapplication.helpers.AlertHelper;
import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.presenters.LoginPresenter;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements LoginPresenter.View{

    private static final String TAG = "LoginActivity";

    private LoginPresenter loginPresenter;

    TextView register;
    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;
    Button guestButton;

    FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPresenter = new LoginPresenter(this);
        firebaseManager = FirebaseManager.getInstance();

        register = findViewById(R.id.register_textView);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
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
                    loginPresenter.signInWith(String.valueOf(emailEditText.getText()), String.valueOf(passwordEditText.getText()));
                }
            }
        });

        guestButton = findViewById(R.id.guest_button);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if(FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
                        loginPresenter.setCurrentUserWithUID(FirebaseAuth.getInstance().getUid());
                        goToSOSActivity();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        loginPresenter.signInAsGuest();
                    }
                } else {
                    loginPresenter.signInAsGuest();
                }
            }
        });


        setTitle("Login");

        if(firebaseManager.isLoggedIn()){
            loginPresenter.setCurrentUserWithUID(FirebaseAuth.getInstance().getUid());
            goToSOSActivity();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

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


    private void  goToSOSActivity() {
        Intent intent = new Intent(LoginActivity.this, SosActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSuccessfulSignIn() {
        goToSOSActivity();
    }

    @Override
    public void onSignInError(String email, String message) {
        AlertHelper.showSimpleAlertDiag(this, "Sign In Error", message);
    }

    @Override
    public void onAnonymousSignInError(String message) {
        AlertHelper.showSimpleAlertDiag(this, "Sign In Error", "Unable to sign in as guest.\n\n" + message);
    }

    @Override
    public void onSuccessfulGuestSignIn() {
        goToSOSActivity();
    }
}
