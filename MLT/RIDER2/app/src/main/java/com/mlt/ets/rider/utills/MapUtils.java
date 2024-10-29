package com.mlt.ets.rider.utills;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.mlt.ets.rider.Helper.UrlManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;



public class MapUtils {


    private UrlManager urlManager; // Declare UrlManager

    // Constructor to initialize UrlManager
    public MapUtils(Context context) {
        this.urlManager = new UrlManager(context); // Instantiate UrlManager here
    }

    public String getStoredAddressFromLatLong(Context context) {
        double latitude = urlManager.getLatitude(); // Retrieve latitude from UrlManager
        double longitude = urlManager.getLongitude(); // Retrieve longitude from UrlManager

        String EmployeeAdress= getAddressFromLatLong(context, latitude, longitude);
        urlManager.setEmployeAddress(EmployeeAdress);
        return EmployeeAdress;
    }


    public static String getAddressFromLatLong(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Customize this to return more specific parts if needed
            } else {
                return "No address found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to get address";
        }
    }
}
