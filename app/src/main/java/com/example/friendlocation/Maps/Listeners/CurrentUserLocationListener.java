package com.example.friendlocation.Maps.Listeners;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.List;

public class CurrentUserLocationListener implements android.location.LocationListener {
    private LocListenerInterface locListenerInterface;
    @Override
    public void onLocationChanged(@NonNull Location location) {
        locListenerInterface.onLocationChanged(location);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        android.location.LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        android.location.LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        android.location.LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        android.location.LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        android.location.LocationListener.super.onProviderDisabled(provider);
    }
    public void setLocListenerInterface(LocListenerInterface locListenerInterface) {
        this.locListenerInterface = locListenerInterface;
    }
}
