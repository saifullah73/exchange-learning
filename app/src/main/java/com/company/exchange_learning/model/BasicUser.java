package com.company.exchange_learning.model;

public class BasicUser {
    private String name;
    private String gender;
    private String country_name;
    private String city_name;
    private String community;
    private String email;
    private boolean email_verified;
    private String android_device_token;

    public BasicUser(String name, String gender, String country_name, String city_name, String community, String email, String android_device_token) {
        this.name = name;
        this.gender = gender;
        this.country_name = country_name;
        this.city_name = city_name;
        this.community = community;
        this.email = email;
        this.email_verified = false;
        this.android_device_token = android_device_token;
    }

    public BasicUser(String name, String gender, String country_name, String city_name, String community, String email, boolean email_verified, String android_device_token) {
        this.name = name;
        this.gender = gender;
        this.country_name = country_name;
        this.city_name = city_name;
        this.community = community;
        this.email = email;
        this.email_verified = email_verified;
        this.android_device_token = android_device_token;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry_name() {
        return country_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getCommunity() {
        return community;
    }

    public String getEmail() {
        return email;
    }

    public boolean getEmail_verified() {
        return email_verified;
    }

    public String getAndroid_device_token() {
        return android_device_token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public void setAndroid_device_token(String android_device_token) {
        this.android_device_token = android_device_token;
    }
}
