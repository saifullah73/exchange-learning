package com.company.exchange_learning.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class UserProfile implements Serializable {
    String my_overview;
    String my_department;
    List<String> my_skills;
    String my_title;
    String my_university;
    String my_address;
    BasicUser user;

    public UserProfile(){

    }

    public UserProfile(String my_overview, String my_department, List<String> my_skills, String my_title, String my_university, BasicUser user) {
        this.my_overview = my_overview;
        this.my_department = my_department;
        this.my_skills = my_skills;
        this.my_title = my_title;
        this.my_university = my_university;
        this.user = user;
    }

    public UserProfile(String my_overview, String my_department, List<String> my_skills, String my_title, String my_university) {
        this.my_overview = my_overview;
        this.my_department = my_department;
        this.my_skills = my_skills;
        this.my_title = my_title;
        this.my_university = my_university;
    }

    public String getMy_address() {
        return my_address;
    }

    public void setMy_address(String my_address) {
        this.my_address = my_address;
    }

    public String getMy_overview() {
        return my_overview;
    }

    public String getMy_department() {
        return my_department;
    }

    public List<String> getMy_skills() {
        return my_skills;
    }

    public String getMy_title() {
        return my_title;
    }

    public String getMy_university() {
        return my_university;
    }

    public BasicUser getUser() {
        return user;
    }

    public void setMy_overview(String my_overview) {
        this.my_overview = my_overview;
    }

    public void setMy_department(String my_department) {
        this.my_department = my_department;
    }

    public void setMy_skills(List<String> my_skills) {
        this.my_skills = my_skills;
    }

    public void setMy_title(String my_title) {
        this.my_title = my_title;
    }

    public void setMy_university(String my_university) {
        this.my_university = my_university;
    }

    public void setUser(BasicUser user) {
        this.user = user;
    }

    @NonNull
    @Override
    public String toString() {
        String abc = "Overview: " + my_overview + " University: " + my_university + " Title: " + my_title + " Department: " + my_department;
        return abc;
    }
}
