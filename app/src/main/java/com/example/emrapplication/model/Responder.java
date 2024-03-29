package com.example.emrapplication.model;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class Responder extends Person {

    public String firstName;
    public String lastName;
    public CustomLocation currentLocation;

    public Responder() {}

    public Responder(String firstName, String lastName, CustomLocation location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.currentLocation = location;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("currentLocation", currentLocation);

        return result;
    }

    @Override public String toString() {
        return "Responder(firstName: " + firstName + ", lastName: " + lastName + ", currentLocation: " + currentLocation + ")";
    }

}
