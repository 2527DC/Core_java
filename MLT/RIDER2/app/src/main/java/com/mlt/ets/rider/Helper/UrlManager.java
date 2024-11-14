package com.mlt.ets.rider.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mlt.ets.rider.MyApp;
import com.mlt.ets.rider.MyFirebaseMessagingService;

public class UrlManager {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_API_TOKEN = "api_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PHONE_CODE = "phone_code";
    private static final String KEY_ADDrESS = "address";
    private static final String FCM_TOKEN = "fcmtoken";
    private static final String PROFILE_PIC = "profile_pic";

    private static final String DRIVER_PREFS = "driver_prefs";
    private static final String DRIVER_NAME = "driver_name";
    private static final String VEHICLE_TYPE = "vehicle_type";
    private static final String VEHICLE_NUMBER = "vehicle_number";
    private static final String OTP = "otp";

    private SharedPreferences sharedPreferences;





    public UrlManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Method to store API token
    public void storeApiToken(String apiToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_API_TOKEN, apiToken);
        editor.apply();
    }

    // Method to retrieve API token
    public String getApiToken() {
        return sharedPreferences.getString(KEY_API_TOKEN, null);
    }

    // Method to store user ID
    public void storeUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.apply();
    }

    // Method to retrieve user ID
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    // Method to store latitude and longitude
    public void storeLocation(double latitude, double longitude) {
        if (!isLocationStored()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(KEY_LATITUDE, (float) latitude);
            editor.putFloat(KEY_LONGITUDE, (float) longitude);
            editor.apply();
        }
    }

    public boolean isLocationStored() {
        return sharedPreferences.contains(KEY_LATITUDE) && sharedPreferences.contains(KEY_LONGITUDE);
    }

    // Method to retrieve latitude
    public double getLatitude() {
        return sharedPreferences.getFloat(KEY_LATITUDE, 0.0f);
    }

    // Method to retrieve longitude
    public double getLongitude() {
        return sharedPreferences.getFloat(KEY_LONGITUDE, 0.0f);
    }

    // Method to clear all stored data
    public void clearAllData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Method to store user email
    public void storeUserEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    // Method to retrieve user email
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    // Method to store username
    public void storeUsername(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME, username);
        editor.apply();
    }

    // Method to retrieve username
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    // Method to store phone
    public void storePhone(String phone) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PHONE, phone);
        editor.apply();
    }

    // Method to retrieve phone
    public String getPhone() {
        return sharedPreferences.getString(KEY_PHONE, null);
    }

    // Method to store gender
    public void storeGender(String gender) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_GENDER, gender);
        editor.apply();
    }

    // Method to retrieve gender
    public String getGender() {
        return sharedPreferences.getString(KEY_GENDER, null);
    }

    // Method to store phone code
    public void storePhoneCode(String phoneCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PHONE_CODE, phoneCode);
        editor.apply();
    }

    // Method to retrieve phone code
    public String getPhoneCode() {
        return sharedPreferences.getString(KEY_PHONE_CODE, null);
    }

    public void storePhone_code(String phoneCode) {
    }

    public void setEmployeAddress(String employeeAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ADDrESS, employeeAddress);
        editor.apply();
    }

    public String getEmployeAddress() {
        return sharedPreferences.getString(KEY_ADDrESS, null);
    }

  public void setFCMtoken(String token){
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(FCM_TOKEN,token );
      editor.apply();

   }

  public String getFcmToken(){

        return sharedPreferences.getString(FCM_TOKEN, null);
  }

    public void StoreProPic(String string) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_PIC,string );
        editor.apply();
    }
    public String getProfilePic(){

        return sharedPreferences.getString(PROFILE_PIC, null);
    }

    public void StoreDrivedetails(String driverName, String vehicleType, String vehicleNumber, String otp) {

        Log.d("Chethan"," saved the details  of driver "+ " "+ driverName);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DRIVER_NAME, driverName);
        editor.putString(VEHICLE_TYPE, vehicleType);
        editor.putString(VEHICLE_NUMBER, vehicleNumber);
        editor.putString(OTP, otp);
        editor.apply();
    }

    // Getter Methods
    public String getDriverName() {
        return sharedPreferences.getString(DRIVER_NAME, "Not yet assigend"); // Default value is an empty string if not found
    }

    public String getVehicleType() {
        return sharedPreferences.getString(VEHICLE_TYPE, "Not yet assigend"); // Default value is an empty string if not found
    }

    public String getVehicleNumber() {
        return sharedPreferences.getString(VEHICLE_NUMBER, "Not yet assigend"); // Default value is an empty string if not found
    }

    public String getOtp() {
        return sharedPreferences.getString(OTP, "Not yet assigend"); // Default value is an empty string if not found
    }

}
