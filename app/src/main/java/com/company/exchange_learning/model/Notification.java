package com.company.exchange_learning.model;

import java.io.Serializable;

public class Notification implements Serializable {
    public String created_at;
    public String post_id;
    public String proposal_id;
    public String read_at;
    public String platform;

    public Notification(){

    }

    public String getCreated_at() {
        return created_at;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getProposal_id() {
        return proposal_id;
    }

    public String getRead_at() {
        return read_at;
    }

    public String getPlatform() {
        return platform;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public void setProposal_id(String proposal_id) {
        this.proposal_id = proposal_id;
    }

    public void setRead_at(String read_at) {
        this.read_at = read_at;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
