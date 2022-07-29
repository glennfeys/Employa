package com.employa.employa.models;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class ChatMessage extends IdentifyableModel {
    private String text;
    private String name;
    private String photoUrl;
    private Timestamp timestamp;
    private String recipient;

    public ChatMessage() {
    }

    public ChatMessage(String text, String name, String photoUrl, String otherUID) {
        this.text = text;
        this.name = name;
        timestamp = Timestamp.now();
        this.photoUrl = photoUrl;
        this.recipient = otherUID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(text, that.text) &&
                Objects.equals(name, that.name) &&
                Objects.equals(photoUrl, that.photoUrl) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(recipient, that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, name, photoUrl, timestamp, recipient);
    }
}

