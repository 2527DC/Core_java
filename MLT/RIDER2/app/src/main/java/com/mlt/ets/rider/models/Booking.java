package com.mlt.ets.rider.models;

public class Booking {

    private String bookDate;
    private String bookTime;
    private String sourceAddress;
    private String destAddress;
    private String drivingTime;
    private String totalKms;
    private String rideStatus;
    private String status;
    private String bookingId;
    private boolean isCanceled;
    // Getters and Setters
    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    // Constructor
    public Booking( String bookDate, String bookTime, String sourceAddress, String destAddress,String rideStatus,String bookingId) {

        this.bookDate = bookDate;
        this.bookTime = bookTime;
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
        this.drivingTime = drivingTime;
        this.totalKms = totalKms;
        this.rideStatus = rideStatus;
        this.bookingId= bookingId;
    }

    public Booking() {

    }

    // Getters and Setters


    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }

    public String getStatus() { // Add a getter for the status
        return status;
    }

    public void setStatus(String status) { // Add a setter for the status
        this.status = status;
    }
}
