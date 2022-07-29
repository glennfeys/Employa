package com.employa.employa.models;

import android.net.Uri;

import java.io.Serializable;

public class Profile implements Serializable {
    // Required values
    private String username;
    private String email;
    private String password;

    // Optionals
    private String description;
    private Uri profilePicture;

    public Profile(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Profile() {
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfilePicture(Uri profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDescription() {
        return description;
    }

    public Uri getProfilePicture() {
        return profilePicture;
    }

}
