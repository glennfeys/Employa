package com.employa.employa.repository;

import android.util.Log;

import com.employa.employa.models.AuthenticationUser;
import com.employa.employa.models.Profile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationRepository {
    private FirebaseAuth auth;
    private static final String TAG = "Authentication";
    private static final String DEFAULT_PROFILE_PIC = "gs://employa-ugent.appspot.com/profile_pictures/default.jpg";

    public AuthenticationRepository(FirebaseAuth auth) {
        this.auth = auth;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    /**
     * Creates the user in the authentication, not yet in database
     * @param profile User profile
     * @return Task
     */
    public Task<AuthResult> createUser(final Profile profile) {
        return auth.createUserWithEmailAndPassword(profile.getEmail(), profile.getPassword());
    }

    public Task<Void> addToFirebase(String uid, String username, String description, String downloadUri) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("name", username);
        user.put("profilePicture", downloadUri != null ? downloadUri : DEFAULT_PROFILE_PIC);
        user.put("description", description);

        return db.collection("users").document(uid).set(user);
    }

    public Task<AuthResult> signInUser(AuthenticationUser user) {
        String email = user.getEmail();
        String password = user.getPassword();

        return auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                });
    }
}

