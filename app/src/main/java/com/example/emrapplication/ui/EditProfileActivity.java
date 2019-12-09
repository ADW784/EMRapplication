package com.example.emrapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.emrapplication.R;
import com.example.emrapplication.helpers.StringHelper;
import com.example.emrapplication.model.Caller;
import com.example.emrapplication.presenters.UserInfoPresenter;

public class EditProfileActivity extends AppCompatActivity implements UserInfoPresenter.View {

    UserInfoPresenter userInfoPresenter;
    Caller currentUser;

    View.OnClickListener cancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    View.OnClickListener updateProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(validateInputs()) {
                updateCurrentUser();
                userInfoPresenter.updateCallerProfile(currentUser);
                onBackPressed();
            }
        }
    };

    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText allergiesEditText;
    EditText medicineEditText;
    EditText doctorEditText;
    Button updateProfileButton;
    Button cancelUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstNameEditText = findViewById(R.id.firstname_editText);
        lastNameEditText = findViewById(R.id.lastname_editText);
        allergiesEditText = findViewById(R.id.allergies_editText);
        medicineEditText = findViewById(R.id.medicine_editText);
        doctorEditText = findViewById(R.id.doctor_editText);
        updateProfileButton = findViewById(R.id.update_profile_button);
        updateProfileButton.setOnClickListener(updateProfileListener);
        cancelUpdateButton = findViewById(R.id.cancel_profile_update_button);
        cancelUpdateButton.setOnClickListener(cancelButtonListener);

        userInfoPresenter = new UserInfoPresenter(this);
        userInfoPresenter.getCurrentUser();

        setTitle("Edit Profile");

    }


    private boolean validateInputs() {

        boolean success = true;

        if(String.valueOf(firstNameEditText.getText()).trim().isEmpty()) {
            success = false;
            firstNameEditText.setError("First name cannot be empty!");
        }

        if(String.valueOf(lastNameEditText.getText()).trim().isEmpty()) {
            success = false;
            lastNameEditText.setError("Last name cannot be empty!");
        }

        return success;
    }

    private void updateUI() {
        firstNameEditText.setText(currentUser.firstName);
        lastNameEditText.setText(currentUser.lastName);
        if(currentUser.allergies != null) { allergiesEditText.setText(currentUser.allergies); }
        if(currentUser.medication != null) { medicineEditText.setText(currentUser.medication); }
        if(currentUser.doctor != null) { doctorEditText.setText(currentUser.doctor); }
    }

    private void updateCurrentUser() {
        currentUser.firstName = firstNameEditText.getText().toString();
        currentUser.lastName = lastNameEditText.getText().toString();
        if(StringHelper.validateStringNotEmpty(allergiesEditText.getText().toString()))
            { currentUser.allergies = allergiesEditText.getText().toString(); }
        if(StringHelper.validateStringNotEmpty(medicineEditText.getText().toString()))
        { currentUser.medication = medicineEditText.getText().toString(); }
        if(StringHelper.validateStringNotEmpty(doctorEditText.getText().toString()))
        { currentUser.doctor = doctorEditText.getText().toString(); }

    }


    @Override
    public void onUserChanged(Caller caller) {
        this.currentUser = caller;
        updateUI();
    }
}
