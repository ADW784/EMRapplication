package com.example.emrapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.emrapplication.R;
import com.example.emrapplication.model.Caller;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.model.EmergencyStatus;
import com.example.emrapplication.presenters.EmergencyPresenter;
import com.example.emrapplication.presenters.UserInfoPresenter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EmergencyActivity extends AppCompatActivity implements EmergencyPresenter.View {

    EmergencyPresenter emergencyPresenter;
    Emergency currentEmergency;

    // TODO: - Add UI Elements and complete updateUI function and go back to SOS function
    TextView timestampTextView;
    TextView statusTextView;
    TextView descriptionTextView;
    TextView responderInfoTextView;
    Button cancelEmergencyButton;

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

        emergencyPresenter = new EmergencyPresenter(this);
        emergencyPresenter.getEmergencyDetailsForCurrentUser();

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
            }
        }

        responderInfoTextView.setText(responderInfo);

    }

    private void goBackToSosActivity() {
        onBackPressed();
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
}
