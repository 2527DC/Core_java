package com.mlt.ets.rider.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mlt.ets.rider.models.Booking;

import java.util.List;

public class BookingHistoryViewModel extends ViewModel {

    private final MutableLiveData<List<Booking>> bookingHistory;

    public BookingHistoryViewModel() {
        bookingHistory = new MutableLiveData<>();
        loadBookingHistory(); // Fetch data from the backend
    }

    public LiveData<List<Booking>> getBookingHistory() {
        return bookingHistory;
    }

    private void loadBookingHistory() {
        // TODO: Fetch booking history from the backend API and update LiveData
        // For example:
        // List<Booking> bookings = fetchFromBackend();
        // bookingHistory.setValue(bookings);
    }
}
