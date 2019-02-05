package com.auth.face.faceauth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.auth.face.faceauth.base.LocationReadyListener;
import com.auth.face.faceauth.logger.LoggerInstance;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationController implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = FaceAuthApp.Companion.getTAG() + ":" + LocationController.class.getSimpleName();

    private GoogleApiClient googleApiClient;
    private Context context;
    private LocationReadyListener listener;

    public LocationController(Context context, LocationReadyListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public static boolean isLocationServiceEnabled(Context context) {
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }

        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            // do nothing
        }

        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            // do nothing
        }

        return gpsEnabled || networkEnabled;
    }

    public void requestCurrentLocation() {
        LoggerInstance.get().info(TAG, "requestCurrentLocation");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (googleApiClient.isConnected()) {
            getCurrentLocation();
        } else {
            googleApiClient.connect();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LoggerInstance.get().info(TAG, "getCurrentLocation");
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            LoggerInstance.get().info(TAG, "getCurrentLocation -> current location is available: lat = " + location.getLatitude() + " lng = " + location.getLongitude());
            listener.onReady(location);
            return;
        }

        LocationRequest request =
                LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LoggerInstance.get().info(TAG, "requestCurrentLocation -> location: lat = " + location.getLatitude() + " lng = " + location.getLongitude());
                listener.onReady(location);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LoggerInstance.get().info(TAG, "onConnected");
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        LoggerInstance.get().info(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LoggerInstance.get().info(TAG, "onConnectionFailed");
    }

}
