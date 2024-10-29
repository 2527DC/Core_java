package com.mlt.ets.rider.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mlt.ets.rider.models.Booking;

import java.util.ArrayList;
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
        // For now, using dummy data for illustration
        List<Booking> bookings = generateDummyData();
        bookingHistory.setValue(bookings);
    }

    // Method to generate dummy booking data
    private List<Booking> generateDummyData() {
        List<Booking> dummyBookings = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            dummyBookings.add(new Booking(
                    "ID" + i,
                    "2024-10-28",
                    "06:35:24",
                    "123 Main St, City, State",
                    "456 Elm St, City, State"
            ));
        }
        return dummyBookings;
    }
}
