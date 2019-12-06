package com.example.emrapplication.presenters;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationPresenter{

    public interface LocationListener {
        void didGetLastLocation(Location location);
        void didFailToGetLastLocation(String message);
        void requestLocationPermission();

    }

    private LocationListener locationListener;
    private Context context;
    private static final String TAG = "MDB:LocationPresenter";

    public LocationPresenter(LocationListener listener, Context context) {
        this.locationListener = listener;
        this.context = context;
    }

    public void getLastLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    Log.d(TAG, "getLastLocation:onSuccess: Location: " + location);
                    locationListener.didGetLastLocation(location);
                } else {
                    Log.d(TAG, "getLastLocation:onSuccess: location is null!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getLastLocation:onFailure: Location failure: error: " + e.getLocalizedMessage());
                locationListener.didFailToGetLastLocation(e.getLocalizedMessage());
            }
        });

    }

}
