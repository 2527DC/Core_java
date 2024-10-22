package com.mlt.ets.rider.viewModel;

public class SignUpRequest {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String name;
    private String password;
    private String email;
    private String gender;

    public SignUpRequest(String name, String password, String email, String gender) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.gender = gender;
    }

    // Getters and setters (if needed)
}
