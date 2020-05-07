package com.company.exchange_learning.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostModel implements Serializable {
    public String post_body;
    public String post_date;
    public List<String> tagged_communities;
    public String post_title;
    public String post_type;
    public String show_skills;
    public String user_id;
    public String post_id;
    public String post_image;
    public String post_image_info;
    public String post_user_posted_image;
    public String post_user_posted_name;

    public PostModel() {
    }



    public PostModel(String post_body, String post_date, List<String> tagged_communities, String post_title, String post_type, String show_skills, String user_id, String post_id, String post_image, String post_image_info, String post_user_posted_image, String post_user_posted_name) {
        this.post_body = post_body;
        this.post_date = post_date;
        this.tagged_communities = tagged_communities;
        this.post_title = post_title;
        this.post_type = post_type;
        this.show_skills = show_skills;
        this.user_id = user_id;
        this.post_id = post_id;
        this.post_image = post_image;
        this.post_image_info = post_image_info;
        this.post_user_posted_image = post_user_posted_image;
        this.post_user_posted_name = post_user_posted_name;
    }

    public static PostModel getPostMode(int type) {
        PostModel post = new PostModel();
        if (type == 1) {
            post.post_body = null;
            post.post_title = null;
            post.tagged_communities = new ArrayList<>();
            post.post_date = null;
            post.post_type = null;
            post.user_id = null;
            post.show_skills = null;
        } else {
            post.post_image = null;
            post.post_image_info = null;
            post.tagged_communities = new ArrayList<>();
            post.post_date = null;
            post.post_type = null;
            post.user_id = null;
            post.show_skills = null;
        }
        return post;
    }

    public String getPost_body() {
        return post_body;
    }

    public void setPost_body(String post_body) {
        this.post_body = post_body;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public List<String> getTagged_communities() {
        return tagged_communities;
    }

    public void setTagged_communities(List<String> tagged_communities) {
        this.tagged_communities = tagged_communities;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;
    }

    public String getShow_skills() {
        return show_skills;
    }

    public void setShow_skills(String show_skills) {
        this.show_skills = show_skills;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getPost_image_info() {
        return post_image_info;
    }

    public void setPost_image_info(String post_image_info) {
        this.post_image_info = post_image_info;
    }

    public String getPost_user_posted_image() {
        return post_user_posted_image;
    }

    public void setPost_user_posted_image(String post_user_posted_image) {
        this.post_user_posted_image = post_user_posted_image;
    }

    public String getPost_user_posted_name() {
        return post_user_posted_name;
    }

    public void setPost_user_posted_name(String post_user_posted_name) {
        this.post_user_posted_name = post_user_posted_name;
    }
}

