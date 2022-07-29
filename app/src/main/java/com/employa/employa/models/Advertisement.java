package com.employa.employa.models;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;
import java.util.Objects;

public class Advertisement extends IdentifyableModel {
    private String title;
    private String description;
    private GeoPoint location;
    private String payment;
    private List<Timestamp> timestamps;
    private String category;
    private Timestamp created;
    private DocumentReference ownerRef;
    private User owner;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public List<Timestamp> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Timestamp> timestamps) {
        this.timestamps = timestamps;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public DocumentReference getOwnerRef() {
        return ownerRef;
    }

    public void setOwnerRef(DocumentReference ownerRef) {
        this.ownerRef = ownerRef;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Advertisement)) return false;
        Advertisement that = (Advertisement) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(location, that.location) &&
                Objects.equals(payment, that.payment) &&
                Objects.equals(timestamps, that.timestamps) &&
                Objects.equals(category, that.category) &&
                Objects.equals(created, that.created) &&
                Objects.equals(ownerRef, that.ownerRef) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, location, payment, timestamps, category, created, ownerRef, owner);
    }

    @Override
    @NonNull
    public String toString() {
        return "Advertisement{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                ", payment='" + payment + '\'' +
                ", timestamps=" + timestamps +
                ", category='" + category + '\'' +
                ", created=" + created +
                ", ownerRef=" + ownerRef +
                ", owner=" + owner +
                '}';
    }
}
