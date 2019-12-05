package com.example.emrapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.emrapplication.R;
import com.example.emrapplication.managers.FirebaseManager;
import com.example.emrapplication.presenters.SosPresenter;
import com.google.firebase.auth.FirebaseAuth;

public class SosActivity extends AppCompatActivity implements SosPresenter.View{

    // MARK: - Class Properties - UI Elements

    Button buttonEditProfile;
    ImageButton imageButtonSOS;
    Dialog confirmDialog;
    Button buttonConfirm;
    Button signoutButton;
    Button registerButton;

    // MARK: - Objects
    SosPresenter sosPresenter;
    boolean anonymous = true;

    // MARK: - Listeners

    View.OnClickListener sigout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SosActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener goToProfileActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(SosActivity.this, EditProfileActivity.class);
            startActivity(i);
        }
    };


    // MARK: - Override Default Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        sosPresenter = new SosPresenter(this);

        signoutButton = findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(sigout);


        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonEditProfile.setOnClickListener(goToProfileActivity);

        registerButton = findViewById(R.id.register_button);

        confirmDialog = new Dialog(SosActivity.this);
        imageButtonSOS = findViewById(R.id.imageButtonSOS);
        imageButtonSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showConfirmPopup();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtonVisibility();
    }

    // MARK: - Class Methods

    private void setButtonVisibility() {
        //if(FirebaseManager.getInstance().isLoggedInAnonymously()) {
        if(FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            signoutButton.setVisibility(View.GONE);
            buttonEditProfile.setVisibility(View.GONE);
            registerButton.setVisibility(View.VISIBLE);
        } else {
            signoutButton.setVisibility(View.VISIBLE);
            buttonEditProfile.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.GONE);
        }
    }

    public void showConfirmPopup(){
        confirmDialog.setContentView(R.layout.confirm_emergency);
        buttonConfirm = confirmDialog.findViewById(R.id.buttonConfirm);

        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmDialog.show();

        try{

            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //this is where the application will proceed with sending the data to the database
                    //for now it will just simply close the dialog/popup.
                    confirmDialog.dismiss();

                    //test code to simply move to the next activity.
                    Intent i = new Intent(SosActivity.this, EmergencyActivity.class);
                    startActivity(i);
                }
            });

        }
        catch (Exception e){

            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    // MARK: - Implement SosPresenter.View Methods


    @Override
    public void onCurrentUserRetrieved(String username) {

    }

    @Override
    public void onCurrentUserRetrievedError(String message) {

    }

    @Override
    public void onSuccessfulSignOut() {

    }
}
