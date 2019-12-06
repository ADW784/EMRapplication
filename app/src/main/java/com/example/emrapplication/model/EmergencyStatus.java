package com.example.emrapplication.model;

public enum EmergencyStatus {

    CREATED("created"), INPROGRESS("in_progress"), RESOLVED("resolved");

    private String status;

    EmergencyStatus(String status) { this.status = status; }

    public String toString() { return this.status; }

}
