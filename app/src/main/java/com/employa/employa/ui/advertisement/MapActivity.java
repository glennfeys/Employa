package com.employa.employa.ui.advertisement;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.employa.employa.R;
import com.employa.employa.models.MyLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final float STRONG_ZOOM = 15;
    private static final String LOCATION = "location";
    private static final String TAG = "MAPS";

    private static final int PERM_REQUEST_CODE = 128;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private EditText mSearchText;
    private MyLocation mCurrentLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Fused location provider client
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mSearchText = findViewById(R.id.search_bar_maps);
        ImageView searchButton = findViewById(R.id.ic_magnify);
        Button locationButton = findViewById(R.id.select_location_button);

        searchButton.setOnClickListener(v -> onClickSearch());
        locationButton.setOnClickListener(v -> {
            if (mCurrentLocation != null) {
                Intent intent = new Intent();
                intent.putExtra(LOCATION, mCurrentLocation);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.select_a_location), Toast.LENGTH_SHORT).show();
            }
        });
        mSearchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE)
                onClickSearch();

            // Nog steeds doorvallen naar default actie
            return false;
        });

        loadMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERM_REQUEST_CODE) {
            for(int x : grantResults) {
                if(x == PackageManager.PERMISSION_DENIED)
                    return;
            }

            setMyLocation();
        }
    }

    private void checkPermission() {
        boolean finePerms = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarsePerms = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // Vraag permissions indien we er geen hebben
        if(finePerms && coarsePerms)
            setMyLocation();
        else
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, PERM_REQUEST_CODE);
    }

    private void loadMap() {
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    private void onClickSearch() {
        mMap.clear();
        geoLocate(mSearchText.getText().toString());
    }

    private void geoLocate(String address) {
        Geocoder geocode = new Geocoder(this);
        List<Address> address_list = new ArrayList<>();

        try {
            address_list = geocode.getFromLocationName(address, 1);
        } catch (IOException e) {
           Log.e(TAG, "Exception: " + e.getMessage());
        }

        if (address_list.size() > 0) {
            Address selectedAddress = address_list.get(0);
            LatLng ll = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());
            saveLocation(selectedAddress.getAddressLine(0), ll);
        } else {
            mCurrentLocation = null;
            Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show();
        }
    }

    private void setMyLocation() {
        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            // Kan (zie android docs)
            if (location == null) return;

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

            Geocoder geocode = new Geocoder(this);
            try {
                List<Address> addresses = geocode.getFromLocation(ll.latitude, ll.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                saveLocation(address, ll);
            } catch (IOException e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

            mMap.setMyLocationEnabled(true);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Vanaf android 6+ zijn coarse & fine location "dangerous permissions".
        // Je moet dan altijd opnieuw vragen achter permissions.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            setMyLocation();
        }
    }

    private void saveLocation(String name, LatLng ll) {
        mCurrentLocation = new MyLocation();
        mCurrentLocation.setLocation(ll);
        mCurrentLocation.setAddress(name);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, STRONG_ZOOM));
        mMap.addMarker(new MarkerOptions().position(ll).title(name));
    }
}
