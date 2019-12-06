package com.example.emrapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.emrapplication.R;
import com.example.emrapplication.model.Caller;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.presenters.EmergencyPresenter;
import com.example.emrapplication.presenters.UserInfoPresenter;

public class EmergencyActivity extends AppCompatActivity implements EmergencyPresenter.View {

    EmergencyPresenter emergencyPresenter;
    Emergency currentEmergency;

    // TODO: - Add UI Elements and complete updateUI function and go back to SOS function

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        emergencyPresenter = new EmergencyPresenter(this);
        emergencyPresenter.getEmergencyDetailsForCurrentUser();


    }

    private void updateUI(Emergency emergency) {

    }

    private void goBackToSosActivity() {

    }

    @Override
    public void didRetrieveEmergencyDetails(Emergency emergency) {
        updateUI(emergency);
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
