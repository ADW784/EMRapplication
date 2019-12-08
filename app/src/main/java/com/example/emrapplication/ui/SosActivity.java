package com.example.emrapplication.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emrapplication.R;
import com.example.emrapplication.managers.Constants;
import com.example.emrapplication.model.Caller;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.presenters.LocationPresenter;
import com.example.emrapplication.presenters.SosPresenter;
import com.example.emrapplication.presenters.UserInfoPresenter;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;

public class SosActivity extends AppCompatActivity implements SosPresenter.View, UserInfoPresenter.View, LocationPresenter.LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    // MARK: - Class Properties - UI Elements

    Button buttonEditProfile;
    ImageButton imageButtonSOS;
    Dialog confirmDialog;
    Button buttonConfirm;
    Button signoutButton;
    Button registerButton;
    TextView userNameTextView;
    EditText descriptionEditText;

    // MARK: - Objects
    SosPresenter sosPresenter;
    UserInfoPresenter userInfoPresenter;
    LocationPresenter locationPresenter;
    Location lastLocation = null;


    private static final String TAG = "MDB:SosActivity";


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

    View.OnClickListener goToRegistrationActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SosActivity.this, RegistrationActivity.class);
            startActivity(intent);
        }
    };


    // MARK: - Override Default Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        checkLoginStatus();

        sosPresenter = new SosPresenter(this);
        userInfoPresenter = new UserInfoPresenter(this);
        sosPresenter.checkIfEmergencyExistsForCurrentUser();

        descriptionEditText = findViewById(R.id.editTextEmergencyDiscription);

        signoutButton = findViewById(R.id.signout_button);
        signoutButton.setOnClickListener(sigout);


        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        buttonEditProfile.setOnClickListener(goToProfileActivity);

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(goToRegistrationActivity);

        userNameTextView = findViewById(R.id.firstname_textView);

        confirmDialog = new Dialog(SosActivity.this);
        imageButtonSOS = findViewById(R.id.imageButtonSOS);
        imageButtonSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showConfirmPopup();
            }
        });

        requestLocationPermission();


        locationPresenter = new LocationPresenter(this, this);
        locationPresenter.getLastLocation();

        setTitle("SOS");

    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtonVisibility();
        userInfoPresenter.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == Constants.REQUEST_CHECK_SETTINGS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: intent data for request check settings: " + data.getDataString());
            } else {
                Log.d(TAG, "onActivityResult: result code not ok! intent data for request check settings: " + data.getDataString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG, "onBackPressed: Override this to do nothing.");
    }

    // MARK: - Class Methods

    private void setButtonVisibility() {

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
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
//                    Intent i = new Intent(SosActivity.this, EmergencyActivity.class);
//                    startActivity(i);
                    //goToEmergencyActivity();
                    sosPresenter.createNewEmergency(lastLocation, descriptionEditText.getText().toString());
                }
            });

        }
        catch (Exception e){

            Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    private void showPermissionAlert() {
        // create an alert dialogue builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // configure title and message for the alert
        builder.setTitle("Location Access Permission");
        builder.setMessage("The app requires your permission to access your location in order to allow emergency personnel to find you.");

        // add cancel button
        builder.setNegativeButton("Do not Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // add grant access button
        builder.setPositiveButton("Grant Access", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(SosActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        });

        // create and show alert
        builder.create().show();
    }

    private void goToEmergencyActivity(Emergency emergency) {
        Intent intent = new Intent(SosActivity.this, EmergencyActivity.class);
        intent.putExtra("emergencyId", emergency.id);
        startActivity(intent);
    }


    private void checkLoginStatus() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(SosActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    // MARK: - Implement ActivityCompat.OnRequestPermissionsResultCallback Methods

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: request code:" + requestCode);
        for(int result: grantResults) {
            Log.d(TAG, "onRequestPermissionsResult: GrantResult: " + result);
        }
        if(requestCode == Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: permission granted, get location.");
                locationPresenter.getLastLocation();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: permission not granted, showing alert. grant results: " + grantResults );
                //showPermissionAlert();
            }
        } else if(requestCode == Constants.PERMISSIONS_REQUEST_ACCESS_BACKGROUND_LOCATION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: permission granted for background location.");
                locationPresenter.getLastLocation();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: background permission not granted, showing alert.");
                //showPermissionAlert();
            }
        }
    }


    // MARK: - Implement LocationPresenter.LocationListener Methods

    @Override
    public void didGetLastLocation(Location location) {
        this.lastLocation = location;
    }

    @Override
    public void didFailToGetLastLocation(String message) {

    }

    @Override
    public void requestLocationPermission() {
        // check if permission has already been given
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted

            // check if you need to show rational behind asking for permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Log.d(TAG, "requestPermission: show permission alert");
                showPermissionAlert();

            } else {
                ActivityCompat.requestPermissions(SosActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);

            }

        } else {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // check if you need to show rational behind asking for permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

                    Log.d(TAG, "requestPermission: show permission alert");
                    showPermissionAlert();

                } else {
                    ActivityCompat.requestPermissions(SosActivity.this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION);

                }
            }
        }
    }

    @Override
    public void didUpdateLocation(LocationResult locationResult) {

    }

    // MARK: - Implement SosPresenter Methods

    @Override
    public void didCreateEmergency(Emergency emergency) {
        goToEmergencyActivity(emergency);
    }

    @Override
    public void errorCreatingEmergency(String message) {
        Toast.makeText(this, "Error creating emergency, please try again!\n\n error:" + message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void emergencyExistsForCurrentUser(Emergency emergency) {
        goToEmergencyActivity(emergency);
    }

    // MARK: - Implement UerInfoPresenter Methods


    @Override
    public void onUserChanged(Caller caller) {
        if(caller.firstName != null && !caller.firstName.isEmpty()) {
            userNameTextView.setText(caller.firstName);
        } else {
            userNameTextView.setText("Guest");
        }
    }


    // MARK: Test
    public void test() {

        sosPresenter.createNewEmergency(lastLocation, "testing");

//        Emergency emergency = new Emergency("1",(new Date()).getTime(),"2", null,"test","testing",lastLocation);
//
//        FirebaseManager.getInstance().DATABASE_REFERENCE.child("emergency_test").setValue(emergency).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "onSuccess: test successfull!");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: test failed!!");
//            }
//        });
//
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("test", "ok then");



    }

}
