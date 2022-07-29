package com.employa.employa.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChangeEmailViewmodel extends ViewModel {

    private MutableLiveData<String> currentEmail = new MutableLiveData<>();
    private MutableLiveData<String> newEmail = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();

    public ChangeEmailViewmodel() {
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        currentEmail.setValue(email);
        newEmail.setValue("");
        password.setValue("");
    }

    public MutableLiveData<String> getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail.setValue(newEmail);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public MutableLiveData<String> getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail.setValue(currentEmail);
    }
}
