package com.employa.employa.ui.auth;

public interface AuthStateListener {
    void onAuthenticationSuccessful();
    void setBackEnabled(boolean b);
}
