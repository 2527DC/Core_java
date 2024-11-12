package com.mlt.ets.rider.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    // MutableLiveData for user profile information
    private final MutableLiveData<String> userName;
    private final MutableLiveData<String> phoneNumber;
    private final MutableLiveData<String> email;

    public ProfileViewModel() {
        userName = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        email = new MutableLiveData<>();

        // Initialize with default values or fetch from a data source
        userName.setValue("Abhishek");
        phoneNumber.setValue("+1234567890");
        email.setValue("john.doe@example.com");
    }

    // Getters for LiveData objects
    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    // Optionally, add setters to update the user data
    public void setUserName(String name) {
        userName.setValue(name);
    }

    public void setPhoneNumber(String phone) {
        phoneNumber.setValue(phone);
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }
}
