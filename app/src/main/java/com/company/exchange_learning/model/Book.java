package com.company.exchange_learning.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Book implements Serializable {
    public String book_title;
    public String book_description;
    public String book_type;
    public String user_id;
    public String book_price;
    public List<String> tagged_communities;
    public String book_address;
    public String cover_photo;
    public String book_id;

    public Book() {
    }

    public Book(ArrayList<String> s){
        tagged_communities = s;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public void setBook_description(String book_description) {
        this.book_description = book_description;
    }

    public void setBook_type(String book_type) {
        this.book_type = book_type;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setBook_price(String book_price) {
        this.book_price = book_price;
    }

    public void setTagged_communities(List<String> tagged_communities) {
        this.tagged_communities = tagged_communities;
    }

    public void setBook_address(String book_address) {
        this.book_address = book_address;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }

    public String getBook_title() {
        return book_title;
    }

    public String getBook_description() {
        return book_description;
    }

    public String getBook_type() {
        return book_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getBook_price() {
        return book_price;
    }

    public List<String> getTagged_communities() {
        return tagged_communities;
    }

    public String getBook_address() {
        return book_address;
    }

    public String getCover_photo() {
        return cover_photo;
    }

    public String toString(){
        return book_title+ "  "+ book_price+ "  "+book_type+ "  "+book_description;
    }
}
