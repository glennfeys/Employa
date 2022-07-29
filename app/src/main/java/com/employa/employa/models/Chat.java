package com.employa.employa.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Objects;

public class Chat extends IdentifyableModel {
    private List<String> users;
    private Timestamp lastActivity;
    private String lastMessage;

    @Exclude
    private CollectionReference messages;

    @Exclude
    private User otherUser;

    public Chat() {
    }

    public Chat(List<String> users, Timestamp lastActivity, String lastMessage) {
        this.users = users;
        this.lastActivity = lastActivity;
        this.lastMessage = lastMessage;
    }

    @Exclude
    public User getOtherUser() {
        return otherUser;
    }

    @Exclude
    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(final List<String> users) {
        this.users = users;
    }

    @Exclude
    public CollectionReference getMessages() {
        return messages;
    }

    @Exclude
    public void setMessages(CollectionReference messages) {
        this.messages = messages;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Timestamp lastActivity) {
        this.lastActivity = lastActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(users, chat.users) &&
                Objects.equals(lastActivity, chat.lastActivity) &&
                Objects.equals(lastMessage, chat.lastMessage) &&
                Objects.equals(messages, chat.messages) &&
                Objects.equals(otherUser, chat.otherUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, lastActivity, lastMessage, messages, otherUser);
    }
}
