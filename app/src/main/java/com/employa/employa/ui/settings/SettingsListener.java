package com.employa.employa.ui.settings;

public interface SettingsListener {
    /**
     * Logs the user out.
     */
    void logOut();

    /**
     * Change email
     */
    void changeEmail();

    /**
     * Change password
     */
    void changePassword();
}
