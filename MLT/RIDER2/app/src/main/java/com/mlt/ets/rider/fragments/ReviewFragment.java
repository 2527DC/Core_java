package com.mlt.ets.rider.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mlt.ets.rider.R;

public class ReviewFragment extends Fragment {

    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button submitReviewButton;

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

        // Set a click listener for the submit button
        submitReviewButton.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        // Get the rating and review text
        float rating = ratingBar.getRating();
        String reviewText = reviewEditText.getText().toString().trim();

        // Validate the inputs
        if (rating == 0) {
            Toast.makeText(getContext(), "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(reviewText)) {
            Toast.makeText(getContext(), "Please write a review", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the review object (You can create a class to represent the review)
        // Send the review data to the backend or save locally

        // Example: Logging the review data
        // You can replace this with your actual API call or logic to save the review.
        String reviewMessage = "Rating: " + rating + "\nReview: " + reviewText;
        Toast.makeText(getContext(), "Review Submitted: " + reviewMessage, Toast.LENGTH_LONG).show();

        // Clear the input fields after submission
        ratingBar.setRating(0); // Reset rating
        reviewEditText.setText(""); // Clear review text
    }
}
