package com.company.exchange_learning.model;

public class UserModel {
    private String android_device_token;
    private String city_name;
    private String community;
    private String country_name;
    private String email;
    private String email_verified;
    private String gender;
    private String name;

    public UserModel() {
    }

    public UserModel(String android_device_token, String city_name, String community, String country_name, String email, String email_verified, String gender, String name) {
        this.android_device_token = android_device_token;
        this.city_name = city_name;
        this.community = community;
        this.country_name = country_name;
        this.email = email;
        this.email_verified = email_verified;
        this.gender = gender;
        this.name = name;
    }

    public String getAndroidDeviceToken() {
        return android_device_token;
    }

    public void setAndroidDeviceToken(String android_device_token) {
        this.android_device_token = android_device_token;
    }

    public String getCityName() {
        return city_name;
    }

    public void setCityName(String city_name) {
        this.city_name = city_name;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getCountryName() {
        return country_name;
    }

    public void setCountryName(String country_name) {
        this.country_name = country_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailVerified() {
        return email_verified;
    }

    public void setEmailVerified(String email_verified) {
        this.email_verified = email_verified;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
