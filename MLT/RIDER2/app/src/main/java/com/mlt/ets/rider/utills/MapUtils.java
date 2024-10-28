package com.mlt.ets.rider.utills;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapUtils {


    public static String getAddressFromLatLong(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // You can customize this to return more specific parts if needed
            } else {
                return "No address found";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Unable to get address";
        }
    }
}
