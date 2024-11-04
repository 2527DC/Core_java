    package com.mlt.ets.rider.adapters;

    import android.annotation.SuppressLint;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;
    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import com.mlt.ets.rider.R;
    import com.mlt.ets.rider.models.Booking;

    import java.util.List;

    public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

        private List<Booking> bookingList;
        private OnBookingCancelListener cancelListener; // Listener for cancel event

        public BookingHistoryAdapter(List<Booking> bookingList, OnBookingCancelListener cancelListener) {
            this.bookingList = bookingList;
            this.cancelListener = cancelListener; // Set the cancel listener
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_booking, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Booking booking = bookingList.get(position);

            // Set the values for each TextView based on the booking details
            holder.textBookingDate.setText("Date: " + booking.getBookDate());
            holder.textBookingTime.setText("Time: " + booking.getBookTime());
            holder.textSourceAddress.setText("Source: " + booking.getSourceAddress());
            holder.textDestAddress.setText("Destination: " + booking.getDestAddress());
            holder.textStatus.setText(booking.getRideStatus());

            // Set the cancel button's click listener
            holder.cancelButton.setOnClickListener(v -> {
                if (cancelListener != null) {
                    cancelListener.onCancelBooking(booking.getBookingId());
                    Log.d("ChEThan",booking.getBookingId());
                }
            });
        }

        @Override
        public int getItemCount() {
            return bookingList.size();
        }

        public void updateBookingList(List<Booking> newBookingList) {
            this.bookingList = newBookingList;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textBookingDate;
            TextView textBookingTime;
            TextView textSourceAddress;
            TextView textDestAddress;
            TextView textStatus;
            Button cancelButton; // Add a button for canceling the booking

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textBookingDate = itemView.findViewById(R.id.textBookingDate);
                textBookingTime = itemView.findViewById(R.id.textBookingTime);
                textSourceAddress = itemView.findViewById(R.id.textSourceAddress);
                textDestAddress = itemView.findViewById(R.id.textDestAddress);
                textStatus = itemView.findViewById(R.id.textStatus);
                cancelButton = itemView.findViewById(R.id.cancelButton); // Initialize the cancel button
            }
        }

        // Define an interface for the cancellation callback
        public interface OnBookingCancelListener {
            void onCancelBooking(String bookingId);
        }
    }
