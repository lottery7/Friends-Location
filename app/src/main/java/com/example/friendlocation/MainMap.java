package com.example.friendlocation;

import static com.example.friendlocation.util.FirebaseUtil.makeEventsMarkers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.friendlocation.util.Pair;
import com.example.friendlocation.util.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainMap extends BottomBar implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = "MainMapTag";
    private String mapMode = "default";
    private Place place = new Place("place");
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //   --- new features for select mode ---

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                mapMode = null;
            } else {
                mapMode = extras.getString("MAP_MODE");
            }
        } else {
            mapMode = (String) savedInstanceState.getSerializable("MAP_MODE");
        }
        Log.w(TAG, "Mode: " + mapMode);

        if (Objects.equals(mapMode, "select_place")) {
            Button selectPlaceBtn = findViewById(R.id.select_place_btn);
            selectPlaceBtn.setVisibility(View.VISIBLE);

            LinearLayout BottomBarLL = findViewById(R.id.bottom_bar_ll);
            BottomBarLL.setVisibility(View.INVISIBLE);
        }

        //   /--- new features for select mode ---/

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
        makeMapMarkers();

        // Select location mode
        if (Objects.equals(mapMode, "select_place")) {
            mMap.setOnMapClickListener(latLng -> {
                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.addMarker(markerOptions);

                Geocoder geocoder = new Geocoder(MainMap.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address curAddress = addresses.get(0);
                        place.description = curAddress.getAddressLine(0);
                        place.coordinates = new Pair<>(curAddress.getLatitude(), curAddress.getLongitude());
                        Toast.makeText(getApplicationContext(), place.description, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
                }
            });
        }
        // Selection end
    }

    private void makeMapMarkers() {
        makeEventsMarkers(mMap);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void acceptPlace(View v) {
        if (place.coordinates == null) {
            return;
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra("PLACE_DESCRIPTION", place.description);
        resultIntent.putExtra("PLACE_LATITUDE", place.coordinates.getFirst().toString());
        resultIntent.putExtra("PLACE_LONGITUDE", place.coordinates.getSecond().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


}

