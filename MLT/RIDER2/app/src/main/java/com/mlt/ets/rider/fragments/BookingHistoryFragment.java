package com.mlt.ets.rider.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.ets.rider.R;
import com.mlt.ets.rider.adapters.BookingHistoryAdapter;
import com.mlt.ets.rider.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewBookingHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy data
        List<Booking> bookingList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            bookingList.add(new Booking("Booking #" + i, "Date: 2024-10-" + i));
        }

        // Set the adapter
        adapter = new BookingHistoryAdapter(bookingList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
