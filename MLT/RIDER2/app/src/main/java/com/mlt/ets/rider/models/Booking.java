package com.mlt.ets.rider.models;

public class Booking {
    private String bookingId;
    private String bookDate;
    private String bookTime;
    private String sourceAddress;
    private String destAddress;

    public Booking(String bookingId, String bookDate, String bookTime, String sourceAddress, String destAddress) {
        this.bookingId = bookingId;
        this.bookDate = bookDate;
        this.bookTime = bookTime;
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getBookDate() {
        return bookDate;
    }

    public String getBookTime() {
        return bookTime;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }
}
