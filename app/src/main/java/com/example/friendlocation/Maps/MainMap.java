package com.example.friendlocation.Maps;

import static com.example.friendlocation.Maps.FirebaseMapPart.updateLocation;
import static com.example.friendlocation.Maps.Listeners.MarkerEventsListener.makeEventsMarkers;
import static com.example.friendlocation.Maps.Listeners.MarkerUsersListener.makeUsersMarkers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.friendlocation.BottomBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.friendlocation.Chatroom;
import com.example.friendlocation.ChatroomModel;
import com.example.friendlocation.Maps.Listeners.LocListenerInterface;
import com.example.friendlocation.Maps.Listeners.CurrentUserLocationListener;
import com.example.friendlocation.Maps.Markers.EventMarkerIcon;
import com.example.friendlocation.Maps.Markers.MarkerIcon;
import com.example.friendlocation.R;
import com.example.friendlocation.utils.FirebaseUtils;
import com.example.friendlocation.utils.Pair;
import com.example.friendlocation.utils.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainMap extends BottomBar implements OnMapReadyCallback, LocListenerInterface {
    private GoogleMap mMap;
    private Marker lastMarker = null;
    private final String TAG = "MainMapTag";
    private String mapMode = "default";
    private Place place = new Place("place");
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LocationManager locationManager;
    private CurrentUserLocationListener currentUserLocationListener;
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
            if (extras != null) {
                mapMode = extras.getString("MAP_MODE");
            }
        } else {
            mapMode = (String) savedInstanceState.getSerializable("MAP_MODE");
        }
        Log.w(TAG, "Mode: " + mapMode);

        if (Objects.equals(mapMode, "select_place")){
            Button selectPlaceBtn = findViewById(R.id.select_place_btn);
            selectPlaceBtn.setVisibility(View.VISIBLE);

            BottomNavigationView BottomBar = findViewById(R.id.bottom_bar);
            BottomBar.setVisibility(View.INVISIBLE);
        }
        //   /--- new features for select mode ---/

        //   /--- Bottom Bar ---/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar);
        bottomNavigationView.setSelectedItemId(R.id.map_case);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.events_case) {
                goToEvents();
                return true;
            } else if (itemId == R.id.chats_case) {
                goToChats();
                return true;
            } else if (itemId == R.id.map_case) {
                // Stay in the current MainMap activity
                return true;
            } else if (itemId == R.id.friends_case) {
                goToFriends();
                return true;
            } else if (itemId == R.id.settings_case) {
                goToSetting();
                return true;
            } else {
                return false;
            }
        });
        //   /--- Bottom Bar ---/

    }

    private void removeFullMarker(){
        if (lastMarker != null) {
            MarkerIcon markerIcon = (MarkerIcon) lastMarker.getTag();
            try {
                if (markerIcon.isEvent()) {
                    lastMarker.setIcon(markerIcon.eventMarkerIcon.getBriefMarkerIcon());
                }
                lastMarker = null;
            } catch (ParseException e) {
                Log.e("Map", "Can't make brief marker", e);
            }
        }

    }

    public void setMarkerListener(){
        mMap.setOnMarkerClickListener(marker -> {
            MarkerIcon markerIcon = (MarkerIcon) marker.getTag();
            if (marker.equals(lastMarker)) {
                if (!markerIcon.isEvent()) {
                    return true;
                }
                Context context = getApplicationContext();
                MarkerIcon finalMarkerIcon = markerIcon;
                FirebaseUtils.getChatroomReference(markerIcon.event.chatUID)
                        .get()
                        .addOnCompleteListener(snapshot -> {
                            ChatroomModel chatroomModel = snapshot.getResult().toObject(ChatroomModel.class);
                            if (chatroomModel != null) {
                                Intent intent = new Intent(context, Chatroom.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .putExtra("chatroomID", finalMarkerIcon.event.chatUID);
                                context.startActivity(intent);
                            }
                        });
                return true;
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), mMap.getCameraPosition().zoom));
            removeFullMarker();
            markerIcon = (MarkerIcon) marker.getTag();
            if (markerIcon == null) {
                return false;
            }
            lastMarker = marker;
            try {
                if (markerIcon.isEvent()) {
                    marker.setIcon(markerIcon.eventMarkerIcon.getFullMarkerIcon());
                }
            } catch (ParseException e) {
                Log.e("Map", "Can't make full marker", e);
                return false;
            }
            return true;
        });
        mMap.setOnMapClickListener(latLng -> removeFullMarker());
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        currentUserLocationListener = new CurrentUserLocationListener();
        currentUserLocationListener.setLocListenerInterface(this);
        updateLocationUI();
        getDeviceLocation();
        makeMapMarkers();

        setMarkerListener();

        // Select location mode
        if (Objects.equals(mapMode, "select_place")) {
            mMap.setOnMapClickListener(latLng -> {
                if (lastMarker != null) {
                    lastMarker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                lastMarker = mMap.addMarker(markerOptions);

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
        makeEventsMarkers(mMap, getApplicationContext(), getResources());
        makeUsersMarkers(mMap, getApplicationContext(), getResources());
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 10, currentUserLocationListener);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
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
        } catch (SecurityException e)  {
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


    @Override
    public void onLocationChanged(Location loc) {
        updateLocation(new Pair<>(loc.getLatitude(), loc.getLongitude()));
    }
}

