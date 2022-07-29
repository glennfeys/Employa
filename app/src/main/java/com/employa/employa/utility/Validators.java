package com.employa.employa.utility;

import android.util.Patterns;

import androidx.lifecycle.LiveData;

public final class Validators {
    /**
     * Verify whether an email adress is valid
     * @param target email address
     * @return True if valid, false otherwise
     */
    public static boolean isValidEmail(String target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * Verify whether a name is valid
     * @param name The name
     * @return True if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name.length() >= 3 && name.length() < 30;
    }

    /**
     * Verify whether a password is valid
     * @param pass The password
     * @return True if valid, false otherwise
     */
    public static boolean isValidPassword(String pass) {
        return pass.length() >= 6;
    }

    /**
     * Verify whether a livedata string is empty
     * @param data The livedata string
     * @return True if empty, false otherwise
     */
    public static boolean isEmptyString(LiveData<String> data) {
        String s = data.getValue();
        return s == null || s.isEmpty();
    }
}
