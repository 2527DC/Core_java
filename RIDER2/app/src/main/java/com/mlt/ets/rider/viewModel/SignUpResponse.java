package com.mlt.ets.rider.viewModel;


public class SignUpResponse {
    private boolean success;
    private String message;

    // Getters and setters

    public SignUpResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
