package com.example.emrapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button buttonRegister;
    TextView textViewLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLoginLink = findViewById(R.id.textViewLoginLink);
        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //default code. simply takes you to the next activity.
                Intent i = new Intent(MainActivity.this,Login.class);
                startActivity(i);
            }
        });

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //default code. simply takes you to the next activity.
                Intent i = new Intent(MainActivity.this,SOS.class);
                startActivity(i);

            }
        });
    }
}
