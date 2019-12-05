package com.example.emrapplication.model;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Caller extends Person {
    public String firstName;
    public String lastName;
    public Set<String> allergies;
    public Set<String> medication;

    public Caller() {};

    public Caller(String username, String email, String uid, String firstName, String lastName, Set<String> allergies, Set<String> medication) {
        super(username, email, uid);
        this.firstName = firstName;
        this.lastName = lastName;
        this.allergies = allergies;
        this.medication = medication;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("uid", uid);
        result.put("username", username);
        result.put("email", email);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("allergies", allergies);
        result.put("medication", medication);

        return result;
    }

    @NonNull
    @Override
    public String toString() {

        return
                "Caller(\n" +
                        "uid: " + this.uid + ",\n" +
                        "username: " + this.username + ",\n" +
                        "email: " + this.email + ",\n" +
                        "firstName: " + this.firstName + ",\n" +
                        "lastName: " + this.lastName + ",\n" +
                        "allergies: " + this.allergies + ",\n" +
                        "medication: " + this.medication + "\n" +
                        ")";

    }
}
