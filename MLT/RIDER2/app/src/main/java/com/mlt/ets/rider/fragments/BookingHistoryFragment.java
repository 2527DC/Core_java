    package com.mlt.ets.rider.fragments;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.Observer;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.mlt.ets.rider.R;
    import com.mlt.ets.rider.adapters.BookingHistoryAdapter;
    import com.mlt.ets.rider.models.Booking;

    import java.util.ArrayList;
    import java.util.List;

    public class BookingHistoryFragment extends Fragment implements BookingHistoryAdapter.OnBookingCancelListener {

        private RecyclerView recyclerView;
        private BookingHistoryAdapter adapter;
        private BookingHistoryViewModel bookingHistoryViewModel;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_booking_history, container, false);

            // Initialize the RecyclerView
            recyclerView = view.findViewById(R.id.recyclerViewBookingHistory);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Initialize ViewModel
            bookingHistoryViewModel = new BookingHistoryViewModel(requireContext());

            // Set the adapter with an empty list
            adapter = new BookingHistoryAdapter(new ArrayList<>(), this,getContext()); // Pass this fragment as the listener
            recyclerView.setAdapter(adapter);

            // Observe the LiveData from the ViewModel
            bookingHistoryViewModel.getBookingHistory().observe(getViewLifecycleOwner(), new Observer<List<Booking>>() {
                @Override
                public void onChanged(List<Booking> bookings) {
                    adapter.updateBookingList(bookings); // Update the adapter's data
                }
            });

            return view;
        }




        @Override
        public void onCancelBooking(String bookingId, int position, BookingHistoryAdapter.ViewHolder holder) {

        }
    }
