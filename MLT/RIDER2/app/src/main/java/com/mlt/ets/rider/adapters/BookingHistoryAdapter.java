package com.mlt.ets.rider.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.models.Booking;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private List<Booking> bookingList;

    public BookingHistoryAdapter(List<Booking> bookings) {
        this.bookingList = bookings;
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
        holder.textBookingDetails.setText(booking.getDetails());
        holder.textBookingDate.setText(booking.getDate());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textBookingDetails;
        TextView textBookingDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textBookingDetails = itemView.findViewById(R.id.textBookingDetails);
            textBookingDate = itemView.findViewById(R.id.textBookingDate);
        }
    }
}
