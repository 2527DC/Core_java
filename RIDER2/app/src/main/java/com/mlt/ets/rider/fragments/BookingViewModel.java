package com.mlt.ets.rider.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BookingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is booking ");
    }

    public LiveData<String> getText() {
        return mText;
    }
}