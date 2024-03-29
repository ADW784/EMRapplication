package com.example.emrapplication.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.emrapplication.R;
import com.example.emrapplication.model.Caller;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.model.EmergencyStatus;
import com.example.emrapplication.presenters.EmergencyPresenter;
import com.example.emrapplication.presenters.LocationPresenter;
import com.example.emrapplication.presenters.UserInfoPresenter;
import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmergencyActivity extends AppCompatActivity implements EmergencyPresenter.View, LocationPresenter.LocationListener, TextToSpeech.OnInitListener {

    private static final String TAG = "MDB:EmergencyActivity";

    EmergencyPresenter emergencyPresenter;
    Emergency currentEmergency;
    LocationPresenter locationPresenter;

    TextView timestampTextView;
    TextView statusTextView;
    TextView descriptionTextView;
    TextView responderInfoTextView;
    Button cancelEmergencyButton;

    private TextToSpeech textToSpeech;

    View.OnClickListener cancelEmergencyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            emergencyPresenter.upDateEmergencyStatusForCurrentUser(EmergencyStatus.CANCELLED);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        textToSpeech = new TextToSpeech(this,this);

        emergencyPresenter = new EmergencyPresenter(this);
        emergencyPresenter.getEmergencyDetailsForCurrentUser();
        emergencyPresenter.listenForStatusChanges();

        locationPresenter = new LocationPresenter(this, this);
        locationPresenter.checkLocationSettingsAndStartUpdates(this);

        timestampTextView = findViewById(R.id.timestamp_textView);
        statusTextView = findViewById(R.id.status_textView);
        descriptionTextView = findViewById(R.id.description_textView);
        responderInfoTextView = findViewById(R.id.responder_info_textView);

        cancelEmergencyButton = findViewById(R.id.cancel_emergency_button);
        cancelEmergencyButton.setOnClickListener(cancelEmergencyListener);

        setTitle("Emergency");

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        locationPresenter.stopLocationUpdates();
        //emergencyPresenter.checkIfExistsAndArchiveEmergencyForCurrentUser();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG, "onBackPressed: Override this to do nothing.");
    }

    private void updateUI(Emergency emergency) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(emergency.timestamp);
        timestampTextView.setText(dateFormat.format(date));

        statusTextView.setText(emergency.status);
        descriptionTextView.setText(emergency.description);

        String responderInfo = "";


        if(emergency.responder != null) {

            if(emergency.responder.firstName != null){
                responderInfo = emergency.responder.firstName + " has responded to your emergency.";

                if(emergency.responder.currentLocation != null && emergency.location != null) {
                    Float distance = emergency.location.distanceTo(emergency.responder.currentLocation);
                    Float distanceInKm = distance/1000;
                    String stringDistance = String.format("%1.2f", distanceInKm);
                    responderInfo += " " + emergency.responder.firstName + " is " + stringDistance + "km away.";
                }
            }
        }

        responderInfoTextView.setText(responderInfo);

    }

    private void goBackToSosActivity() {

        //onBackPressed();
        Intent intent = new Intent(EmergencyActivity.this, SosActivity.class);
        startActivity(intent);

    }

    @Override
    public void didRetrieveEmergencyDetails(Emergency emergency) {
        updateUI(emergency);
        currentEmergency = emergency;
    }

    @Override
    public void didArchiveEmergency(Emergency emergency) {

    }

    @Override
    public void didRemoveEmergencyFromActiveList(Emergency emergency) {

    }

    @Override
    public void didRemoveEmergencyFromUser(Emergency emergency) {
        emergencyPresenter.archiveEmergency(emergency);
        emergencyPresenter.removeEmergencyFromActiveList(emergency);
        goBackToSosActivity();
    }

    @Override
    public void errorRemovingEmergencyFromUser(String message) {

    }

    @Override
    public void errorRetrievingEmergencyDetails(String message) {

    }

    @Override
    public void errorArchivingEmergency(String message) {

    }

    @Override
    public void errorRemovingEmergencyFromActiveList(String message) {

    }

    @Override
    public void didCreateEmergency(Emergency emergency) {

    }

    @Override
    public void errorCreatingEmergency(String message) {

    }

    @Override
    public void didUpdateEmergencyStatus(String message) {

        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void didGetLastLocation(Location location) {
        if(currentEmergency != null && currentEmergency.id != null) {
            emergencyPresenter.updateEmergencyLocation(location);
        }
    }

    @Override
    public void didFailToGetLastLocation(String message) {

    }

    @Override
    public void requestLocationPermission() {

    }

    @Override
    public void didUpdateLocation(LocationResult locationResult) {

    }

    @Override
    public void onInit(int status) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
