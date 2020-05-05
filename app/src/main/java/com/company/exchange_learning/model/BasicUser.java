package com.company.exchange_learning.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class BasicUser implements Serializable {
    private String name;
    private String gender;
    private String country_name;
    private String city_name;
    private String community;
    private String email;
    private String android_device_token;

    public BasicUser(){
    }

    public BasicUser(String name, String gender, String country_name, String city_name, String community, String email, String android_device_token) {
        this.name = name;
        this.gender = gender;
        this.country_name = country_name;
        this.city_name = city_name;
        this.community = community;
        this.email = email;
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

    public void setAndroid_device_token(String android_device_token) {
        this.android_device_token = android_device_token;
    }

    @NonNull
    @Override
    public String toString() {
        String abc = "Name: "+name+" Community: "+community+" Gender: "+gender+" Email: "+email+" City: "+city_name+" Country: "+country_name+" Android tokern: "+android_device_token;
        return abc;
    }
}
