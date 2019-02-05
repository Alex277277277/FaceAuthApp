package com.auth.face.faceauth;

import android.content.Context;
import android.location.LocationManager;

public class LocationController {

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

}
