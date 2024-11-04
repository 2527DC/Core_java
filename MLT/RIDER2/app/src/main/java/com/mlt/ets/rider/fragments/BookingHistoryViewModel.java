package com.mlt.ets.rider.fragments;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mlt.ets.rider.Helper.UrlManager;
import com.mlt.ets.rider.models.Booking;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryViewModel extends ViewModel {

    private final MutableLiveData<List<Booking>> bookingHistory;
    private UrlManager urlManager;

    public BookingHistoryViewModel(Context context) {
        bookingHistory = new MutableLiveData<>();
        urlManager = new UrlManager(context); // Initialize UrlManager with context

        loadBookingHistory();
    }

    public LiveData<List<Booking>> getBookingHistory() {
        return bookingHistory;
    }

    private void loadBookingHistory() {
        try {
            // Create JSON object and add user_id from UrlManager
            JSONObject bookingHistoryRequest = new JSONObject();
            bookingHistoryRequest.put("api_token", urlManager.getApiToken());
            bookingHistoryRequest.put("customer_id", urlManager.getUserId());

            RequestBody requestBody = RequestBody.create(
                    bookingHistoryRequest.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.getBookingHistory(requestBody);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String jsonResponse = response.body().string();

                            // Log the JSON response
                            Log.d("BookingHistoryViewModel", "Response JSON: " + jsonResponse);

                            // Parse the bookings from the response
                            List<Booking> bookings = parseBookingsFromResponse(jsonResponse);
                            bookingHistory.setValue(bookings);
                        } catch (Exception e) {
                            Log.e("BookingHistoryViewModel", "Error parsing response", e);
                        }
                    } else {
                        Log.e("BookingHistoryViewModel", "Request failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("BookingHistoryViewModel", "Network request failed", t);
                }
            });
        } catch (JSONException e) {
            Log.e("BookingHistoryViewModel", "JSON error", e);
        }
    }

    private List<Booking> parseBookingsFromResponse(String jsonResponse) {
        List<Booking> bookings = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject dataObject = jsonObject.getJSONObject("data"); // Access the 'data' object
            JSONArray ridesArray = dataObject.getJSONArray("rides");  // Now access 'rides' inside 'data'

            for (int i = 0; i < ridesArray.length(); i++) {
                JSONObject ride = ridesArray.getJSONObject(i);
                Booking booking = new Booking();
                booking.setBookingId(ride.getString("booking_id"));
                booking.setBookDate(ride.getString("book_date"));
                booking.setBookTime(ride.getString("book_time"));
                booking.setSourceAddress(ride.getString("source_address"));
                booking.setDestAddress(ride.getString("dest_address"));
                booking.setRideStatus(ride.getString("ride_status"));
                bookings.add(booking);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public void cancelBooking(String bookingId) {
    }
}
