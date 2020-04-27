package com.company.exchange_learning.model;

public class PostModel {
    public String post_body;
    public String post_date;
    public String post_category;
    public String post_title;
    public String post_type;
    public String show_skills;
    public String user_id;
    public String post_image;
    public String post_image_info;
    public String post_user_posted_image;
    public String post_user_posted_name;

    public PostModel() {
    }

    public String getPostUserPostedImage() {
        return post_user_posted_image;
    }

    public void setPostUserPostedImage(String post_user_posted_image) {
        this.post_user_posted_image = post_user_posted_image;
    }

    public PostModel(String post_body, String post_date, String post_category, String post_title, String post_type, String show_skills, String user_id, String post_image, String post_image_info, String post_user_posted_image, String post_user_posted_name) {
        this.post_body = post_body;
        this.post_date = post_date;
        this.post_category = post_category;
        this.post_title = post_title;
        this.post_type = post_type;
        this.show_skills = show_skills;
        this.user_id = user_id;
        this.post_image = post_image;
        this.post_image_info = post_image_info;
        this.post_user_posted_image = post_user_posted_image;
        this.post_user_posted_name = post_user_posted_name;
    }

    public String getPostUserPostedName() {
        return post_user_posted_name;
    }

    public void setPostUserPostedName(String post_user_posted_name) {
        this.post_user_posted_name = post_user_posted_name;
    }

    public String getPostImage() {
        return post_image;
    }

    public void setPostImage(String post_image) {
        this.post_image = post_image;
    }

    public String getPostImageInfo() {
        return post_image_info;
    }

    public void setPostImageInfo(String post_image_info) {
        this.post_image_info = post_image_info;
    }

    public String getPostBody() {
        return post_body;
    }

    public void setPostBody(String post_body) {
        this.post_body = post_body;
    }

    public String getPostDate() {
        return post_date;
    }

    public void setPostDate(String post_date) {
        this.post_date = post_date;
    }

    public String getPostCategory() {
        return post_category;
    }

    public void setPostCategory(String post_category) {
        this.post_category = post_category;
    }

    public String getPostTitle() {
        return post_title;
    }

    public void setPostTitle(String post_title) {
        this.post_title = post_title;
    }

    public String getPostType() {
        return post_type;
    }

    public void setPostType(String post_type) {
        this.post_type = post_type;
    }

    public String getShowSkills() {
        return show_skills;
    }

    public void setShowSkills(String show_skills) {
        this.show_skills = show_skills;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }
}

