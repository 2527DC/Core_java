package com.mlt.ets.rider.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<LatLng> destination = new MutableLiveData<>();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    // Set destination
    public void setDestination(LatLng latLng) {
        destination.setValue(latLng);
    }

    // Get destination
    public LiveData<LatLng> getDestination() {
        return destination;
    }
}
