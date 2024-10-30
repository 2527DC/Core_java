package com.mlt.ets.rider.fragments;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mlt.ets.rider.databinding.FragmentScheduleBinding;
import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;


public class SchedulFragment extends Fragment {

    private FragmentScheduleBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScheduleViewModel scheduleViewModel =
                new ViewModelProvider(this).get(ScheduleViewModel.class);

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupUI();
        return root;
    }

    private void setupUI() {
        // Set up RadioGroup for booking type selection
        binding.bookingTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.monthlyBooking.getId()) {
                Toast.makeText(getContext(), "Monthly booking selected", Toast.LENGTH_SHORT).show();
            } else if (checkedId == binding.weeklyBooking.getId()) {
                Toast.makeText(getContext(), "Weekly booking selected", Toast.LENGTH_SHORT).show();
            } else if (checkedId == binding.quickBooking.getId()) {
                Toast.makeText(getContext(), "Quick booking selected", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up DatePickerDialog for date selection
        binding.datePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        binding.datePickerButton.setText(date);
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Set up TimePickerDialog for time selection
        binding.timePickerButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute) -> {
                        String time = hourOfDay + ":" + String.format("%02d", minute);
                        binding.timePickerButton.setText(time);
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        });

        // Handle submit booking button
        binding.submitBookingButton.setOnClickListener(v -> {
            String selectedFrequency = binding.frequencySpinner.getSelectedItem().toString();
            Toast.makeText(getContext(), "Booking submitted with frequency: " + selectedFrequency, Toast.LENGTH_SHORT).show();
            // Further booking submission logic
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
