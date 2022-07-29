package com.employa.employa.models;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Objects;

public class User {

    private String id;

    private String name;
    private String description;
    private String profilePicture;
    private Uri profilePicDownload;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Uri getProfilePicDownload() {
        return profilePicDownload;
    }

    public void setProfilePicDownload(Uri profilePicDownload) {
        this.profilePicDownload = profilePicDownload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(description, user.description) &&
                Objects.equals(profilePicture, user.profilePicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, profilePicture);
    }

    @Override
    @NonNull
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                '}';
    }
}
