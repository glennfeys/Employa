package com.employa.employa.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;

public class Rating extends IdentifyableModel {
    private DocumentReference userReference;
    private int score;
    private String description;
    private Timestamp created;
    private User user;

    public Rating() {
    }

    public Rating(DocumentReference userReference, int score, String description, Timestamp created) {
        this.userReference = userReference;
        this.score = score;
        this.description = description;
        this.created = created;
    }

    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        this.userReference = userReference;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating)) return false;
        Rating that = (Rating) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(userReference, that.userReference) &&
                Objects.equals(score, that.score) &&
                Objects.equals(created, that.created);
    }
}
