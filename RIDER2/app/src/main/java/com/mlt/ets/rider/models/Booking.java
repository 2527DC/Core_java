package com.mlt.ets.rider.models;
public class Booking {
    private String details;
    private String date;

    public Booking(String details, String date) {
        this.details = details;
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public String getDate() {
        return date;
    }
}
