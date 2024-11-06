package com.mlt.ets.rider.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mlt.ets.rider.R;
import com.mlt.ets.rider.adapters.SelectedDatesAdapter;
import com.mlt.ets.rider.network.ApiService;
import com.mlt.ets.rider.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchedulFragment extends Fragment implements SelectedDatesAdapter.OnDateRemoveListener {

    private Button datePickerButton, timePickerButton;
    private RecyclerView selectedDatesRecyclerView;
    private SelectedDatesAdapter datesAdapter;
    private Set<Long> selectedDates = new HashSet<>();  // Use Set to avoid duplicates
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        datePickerButton = view.findViewById(R.id.datePickerButton);
        timePickerButton = view.findViewById(R.id.timePickerButton);
        selectedDatesRecyclerView = view.findViewById(R.id.selectedDatesRecyclerView);
        Button submitBookingButton = view.findViewById(R.id.submitBookingButton);

        // Initialize RecyclerView for selected dates
        selectedDatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        datesAdapter = new SelectedDatesAdapter(new ArrayList<>(selectedDates), this);
        selectedDatesRecyclerView.setAdapter(datesAdapter);

        datePickerButton.setOnClickListener(v -> openMultiDatePicker());
        timePickerButton.setOnClickListener(v -> openTimePicker());

        submitBookingButton.setOnClickListener(v -> sendSelectedDatesToBackend());

        return view;
    }

    private void openMultiDatePicker() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long today = calendar.getTimeInMillis();

        // Setting constraints to disable past dates
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(today));

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a Date");
        builder.setCalendarConstraints(constraintsBuilder.build());

        final MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
                // Add selected date if not already in the list
                if (selectedDates.add(selection)) {
                    datesAdapter.updateDates(new ArrayList<>(selectedDates));
                    Log.d("SelectedDate", "Date added: " + dateFormat.format(selection));
                } else {
                    Toast.makeText(getContext(), "Date already selected", Toast.LENGTH_SHORT).show();
                }
            }
            // Reopen date picker for another selection
            openMultiDatePicker();
        });

        datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, selectedHour, selectedMinute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);

            String timeString = timeFormat.format(calendar.getTime());
            // Set the selected time directly on the button
            timePickerButton.setText(timeString);
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void sendSelectedDatesToBackend() {
        if (!selectedDates.isEmpty()) {
            // Create JSON array for dates
            JsonArray datesArray = new JsonArray();
            for (Long date : selectedDates) {
                datesArray.add(dateFormat.format(date));  // format date as string if needed
                Log.d("SelectedDate", "Submitted Date: " + dateFormat.format(date));
            }

            // Create JSON object with additional information
            JsonObject jsonBody = new JsonObject();
            jsonBody.addProperty("userId", "1");
            jsonBody.addProperty("apiToken", "gosjbjh");
            jsonBody.addProperty("frequency", "frequency");
            jsonBody.add("selectedDates", datesArray);  // Add the dates array

            // Convert JSON to RequestBody for Retrofit
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

            // Send POST request using ApiService
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.secduleBooking(requestBody);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d("API Response", "Booking submitted successfully:   " + response.body());
                        Toast.makeText(getContext(), "Booking submitted with selected dates", Toast.LENGTH_LONG).show();
                        // Clear selected dates and update RecyclerView
                        selectedDates.clear();
                        datesAdapter.updateDates(new ArrayList<>(selectedDates));
                    } else {
                        Log.e("API Response", "Failed to submit booking: " + response.errorBody());
                        Toast.makeText(getContext(), "Failed to submit booking", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("API Error", "Network error: " + t.getMessage());
                    Toast.makeText(getContext(), "Network error. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please select at least one date", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateRemoved(int position) {
        // Remove date from the selected dates set
        Long removedDate = new ArrayList<>(selectedDates).get(position);
        selectedDates.remove(removedDate);
        // Update the adapter with the new dates list
        datesAdapter.updateDates(new ArrayList<>(selectedDates));
    }
}
