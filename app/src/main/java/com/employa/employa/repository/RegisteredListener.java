package com.employa.employa.repository;

import com.google.firebase.firestore.ListenerRegistration;

/**
 * Klasse om Firebase weg te abstraheren. Kan intern uitgebreid worden naar meerdere constructors.
 */
public class RegisteredListener {
    private ListenerRegistration listener;

    RegisteredListener(ListenerRegistration listener) {
        this.listener = listener;
    }

    public void remove() {
        assert listener != null; // Kan problemen detecteren
        listener.remove();
        listener = null;
    }
}
