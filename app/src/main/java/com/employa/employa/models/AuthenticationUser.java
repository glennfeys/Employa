package com.employa.employa.models;

public class AuthenticationUser {
    private String email;
    private String password;

    public AuthenticationUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
