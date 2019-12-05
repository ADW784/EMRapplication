package com.example.emrapplication.presenters;

public class SosPresenter {

    public interface View {
        void onCurrentUserRetrieved(String username);
        void onCurrentUserRetrievedError(String message);
        void onSuccessfulSignOut();
        //void onSignInStatusChange();
    }

    View view;

    public SosPresenter(View view) { this.view = view; }

    public void createNewEmergency() {

    }

}
