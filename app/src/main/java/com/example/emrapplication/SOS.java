package com.example.emrapplication;

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

public class SOS extends AppCompatActivity {

    Button buttonEditProfile;
    ImageButton imageButtonSOS;
    Dialog confirmDialog;
    Button buttonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //test code to simply move to the next activity.
                Intent i = new Intent(SOS.this,Profile.class);
                startActivity(i);

            }
        });

        confirmDialog = new Dialog(SOS.this);
        imageButtonSOS = findViewById(R.id.imageButtonSOS);
        imageButtonSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showConfirmPopup();
            }
        });
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
                    Intent i = new Intent(SOS.this,emergency.class);
                    startActivity(i);
                }
            });

        }
        catch (Exception e){

            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }
}
