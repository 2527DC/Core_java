package com.mlt.ets.rider.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mlt.ets.rider.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {

    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button submitReviewButton;
    private Spinner driverSpinner;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the views
        ratingBar = view.findViewById(R.id.ratingBar);
        reviewEditText = view.findViewById(R.id.reviewEditText);
        submitReviewButton = view.findViewById(R.id.submitReviewButton);
        driverSpinner = view.findViewById(R.id.driverSpinner); // Initialize the Spinner

        // Set up the driver spinner with dummy data
        setupDriverSpinner();

        // Set a click listener for the submit button
        submitReviewButton.setOnClickListener(v -> submitReview());
    }

    private void setupDriverSpinner() {
        // Create dummy data for drivers, with a default prompt as the first item
        List<String> driverList = new ArrayList<>();
        driverList.add("Select the driver"); // Default prompt

        for (int i = 1; i <= 5; i++) {
            driverList.add("Driver " + i);
        }

        // Create an ArrayAdapter with the dummy data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, driverList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter to the spinner
        driverSpinner.setAdapter(adapter);
        driverSpinner.setSelection(0); // Set default selection to "Select the driver"
    }


    private void submitReview() {
        // Get the selected driver, rating, and review text
        String selectedDriver = driverSpinner.getSelectedItem().toString();
        float rating = ratingBar.getRating();
        String reviewText = reviewEditText.getText().toString().trim();

        // Validate the inputs
        if (TextUtils.isEmpty(selectedDriver)) {
            Toast.makeText(getContext(), "Please select a driver", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(getContext(), "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(reviewText)) {
            Toast.makeText(getContext(), "Please write a review", Toast.LENGTH_SHORT).show();
            return;
        }

        // Example: Logging the review data
        // Replace this with your actual API call or logic to save the review.
        String reviewMessage = "Driver: " + selectedDriver + "\nRating: " + rating + "\nReview: " + reviewText;
        Toast.makeText(getContext(), "Review Submitted: " + reviewMessage, Toast.LENGTH_LONG).show();

        // Clear the input fields after submission
        driverSpinner.setSelection(0); // Reset driver selection
        ratingBar.setRating(0); // Reset rating
        reviewEditText.setText(""); // Clear review text
    }
}
