package com.employa.employa.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.employa.employa.repository.Callback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationPermissionHelper {
    private Context context;
    private Activity activity;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    /**
     * This class provides an abstraction for the Location APIs
     * Android provides and the permissions it requires
     *
     * @param context  Context of this activity
     * @param activity Activity requesting location data
     */
    public LocationPermissionHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    /**
     * Requests location permission if necessary and gets
     * the last available location from the device
     *
     * @param callback Callback to handle the result of our location request
     */
    public void getLocation(Callback<Location> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(callback::onSuccess).addOnFailureListener(failure -> callback.onFail());
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }
}
