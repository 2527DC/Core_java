package com.mlt.ets.rider.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mlt.ets.rider.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SelectedDatesAdapter extends RecyclerView.Adapter<SelectedDatesAdapter.DateViewHolder> {

    private List<Long> dates;
    private OnDateRemoveListener onDateRemoveListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    // Constructor for the adapter, allows you to set the listener for date removal
    public SelectedDatesAdapter(List<Long> dates, OnDateRemoveListener listener) {
        this.dates = dates;
        this.onDateRemoveListener = listener;
    }

    // Update the list of dates and notify the adapter
    public void updateDates(List<Long> newDates) {
        this.dates = newDates;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        // Set the date text in the TextView
        holder.dateTextView.setText(dateFormat.format(dates.get(position)));

        // Set up the cancel button to remove the date from the list when clicked
        holder.cancelButton.setOnClickListener(v -> {
            if (onDateRemoveListener != null) {
                // Call the listener to remove the date at the given position
                onDateRemoveListener.onDateRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dates == null ? 0 : dates.size();
    }

    // ViewHolder class to hold the views of each date item
    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        Button cancelButton;

        DateViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }

    // Interface for notifying when a date is removed
    public interface OnDateRemoveListener {
        void onDateRemoved(int position);
    }
}
