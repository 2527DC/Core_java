package com.mlt.ets.rider.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private List<Booking> bookingList;

    public BookingHistoryAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList; // Use the booking list passed from the ViewModel
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Set the values for each TextView based on the booking details
        holder.textBookingDate.setText("Date: " + booking.getBookDate());
        holder.textBookingTime.setText("Time: " + booking.getBookTime());
        holder.textSourceAddress.setText("Source: " + booking.getSourceAddress());
        holder.textDestAddress.setText("Destination: " + booking.getDestAddress());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateBookingList(List<Booking> newBookingList) {
        this.bookingList = newBookingList; // Update the list
        notifyDataSetChanged(); // Notify adapter about data change
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textBookingDate;
        TextView textBookingTime;
        TextView textSourceAddress;
        TextView textDestAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textBookingDate = itemView.findViewById(R.id.textBookingDate);
            textBookingTime = itemView.findViewById(R.id.textBookingTime);
            textSourceAddress = itemView.findViewById(R.id.textSourceAddress);
            textDestAddress = itemView.findViewById(R.id.textDestAddress);
        }
    }
}
