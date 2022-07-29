package com.employa.employa.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public final class Helpers {
    /**
     * Is network available?
     *
     * @param activity De activity
     * @return True indien available, false otherwise
     */
    public static boolean isNetworkAvailable(@NonNull Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    /**
     * Restore default keyboard mode
     *
     * @param f Fragment
     */
    public static void restoreDefaultKeyboardMode(Fragment f) {
        setKeyboardMode(f, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * Set keyboard mode
     *
     * @param f    Fragment
     * @param mode Mode
     */
    public static void setKeyboardMode(Fragment f, int mode) {
        f.requireActivity().getWindow().setSoftInputMode(mode);
    }
}
