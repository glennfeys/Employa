package com.employa.employa.utility;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MessagingListener implements FirebaseAuth.AuthStateListener {
    // De auth state change oproep, wordt uitgevoerd op het moment dat de state al gewijzigd is.
    // We kunnen dus bv het id van de user niet meer opvragen op dat moment.
    private String currentUID;

    private String createTopic() {
        return "user_" + currentUID;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if(firebaseAuth.getCurrentUser() == null){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(createTopic());
        } else {
            currentUID = firebaseAuth.getCurrentUser().getUid();
            FirebaseMessaging.getInstance().subscribeToTopic(createTopic());
        }
    }
}
