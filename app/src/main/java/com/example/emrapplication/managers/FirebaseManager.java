package com.example.emrapplication.managers;

import java.sql.Struct;

public class FirebaseManager {

    FirebaseManager instance;

    private FirebaseManager(){};

    public FirebaseManager getInstance() {
        if(instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }


    public static String USERS_REF = "users";


}
