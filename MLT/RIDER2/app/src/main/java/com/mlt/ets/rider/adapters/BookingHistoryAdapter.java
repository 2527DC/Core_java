package com.mlt.ets.rider.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.models.Booking;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {
    private UrlManager urlManager; // Instance of UrlManager to get the API token
    private List<Booking> bookingList;
    private OnBookingCancelListener cancelListener;

    public BookingHistoryAdapter(List<Booking> bookingList, OnBookingCancelListener cancelListener, Context context) {
        this.bookingList = bookingList;
        this.cancelListener = cancelListener; // Set the cancel listener
        this.urlManager = new UrlManager(context); // Initialize UrlManager to access shared preferences
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

        // Set values for each TextView based on the booking details
        holder.textBookingDate.setText("Date: " + booking.ge tBookDate());
        holder.textBookingTime.setText("Time: " + booking.getBookTime());
        holder.textSourceAddress.setText("Source: " + booking.getSourceAddress());
        holder.textDestAddress.setText("Destination: " + booking.getDestAddress());
        holder.textStatus.setText(booking.getRideStatus());

        // Determine button appearance and behavior based on the status or `isCanceled`
        if (booking.isCanceled() || "Cancelled".equals(booking.getRideStatus())) {
            holder.cancelButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            holder.cancelButton.setText("Cancelled");
            holder.cancelButton.setEnabled(false); // Disable the button
            // Adjust width for "Cancelled" text
            ViewGroup.LayoutParams params = holder.cancelButton.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            holder.cancelButton.setLayoutParams(params);
        } else {
            holder.cancelButton.setBackgroundColor(Color.RED);
            holder.cancelButton.setText("Cancel");
            holder.cancelButton.setEnabled(true); // Enable the button


        }

        // Handle cancel button click only if it's enabled
        holder.cancelButton.setOnClickListener(v -> {
            if (!booking.isCanceled()) { // Only allow canceling if not already canceled
                cancelBooking(booking.getBookingId(), position, holder);
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
        Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textBookingDate = itemView.findViewById(R.id.textBookingDate);
            textBookingTime = itemView.findViewById(R.id.textBookingTime);
            textSourceAddress = itemView.findViewById(R.id.textSourceAddress);
            textDestAddress = itemView.findViewById(R.id.textDestAddress);
            textStatus = itemView.findViewById(R.id.textStatus);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }

    public interface OnBookingCancelListener {
        void onCancelBooking(String bookingId, int position, ViewHolder holder);
    }

    // Method to cancel booking (this should be called from your ViewModel or directly in the adapter)
    public void cancelBooking(String bookingId, int position, ViewHolder holder) {
        // Assuming you have an API endpoint for canceling the booking
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Create a request body for cancel booking
        JSONObject cancelBookingRequest = new JSONObject();
        try {
            cancelBookingRequest.put("api_token", urlManager.getApiToken());
            cancelBookingRequest.put("cancel_id", bookingId);
            cancelBookingRequest.put("reason", "User requested cancellation");

            RequestBody requestBody = RequestBody.create(cancelBookingRequest.toString(), MediaType.get("application/json; charset=utf-8"));
            Call<ResponseBody> call = apiService.cancleBooking(requestBody);

            // Make the call asynchronously
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Update the booking state to reflect cancellation
                        bookingList.get(position).setCanceled(true); // Mark booking as canceled
                        notifyItemChanged(position); // Notify the adapter to update the UI

                        // Optionally, update the status of the booking here as well
                        bookingList.get(position).setRideStatus("Cancelled"); // Update the ride status
                    } else {
                        Log.e("BookingHistoryAdapter", "Cancel booking failed");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("BookingHistoryAdapter", "Network failure", t);
                }
            });
        } catch (JSONException e) {
            Log.e("BookingHistoryAdapter", "Error creating JSON for cancel booking", e);
        }
    }

}
