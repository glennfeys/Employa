package com.employa.employa.models;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

public abstract class IdentifyableModel {
    @Exclude
    protected String id;

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public abstract boolean equals(@Nullable Object obj);
}
