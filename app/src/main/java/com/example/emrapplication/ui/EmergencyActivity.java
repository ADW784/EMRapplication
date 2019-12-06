package com.example.emrapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.emrapplication.R;
import com.example.emrapplication.model.Caller;
import com.example.emrapplication.model.Emergency;
import com.example.emrapplication.presenters.UserInfoPresenter;

public class EmergencyActivity extends AppCompatActivity implements UserInfoPresenter.View{

    UserInfoPresenter userInfoPresenter;
    Emergency currentEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        userInfoPresenter = new UserInfoPresenter(this);
        userInfoPresenter.getCurrentUser();


    }


    @Override
    public void onUserChanged(Caller caller) {

    }
}
