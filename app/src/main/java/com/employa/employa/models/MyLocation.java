package com.employa.employa.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class MyLocation implements Serializable {
    private double latitude;
    private double longitude;
    private String address = "";

    public MyLocation() {
        latitude = 0;
        longitude = 0;
    }

    public MyLocation(GeoPoint location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public void setLocation(LatLng location) {
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public void setAddress(String name) {
        this.address = name;
    }

    public String getAddress() {
        return address;
    }
}
