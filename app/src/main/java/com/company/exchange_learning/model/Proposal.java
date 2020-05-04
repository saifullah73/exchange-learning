package com.company.exchange_learning.model;

import java.io.Serializable;

public class Proposal implements Serializable {
    public String accepted;
    public String proposal_data;
    public String proposal_date;
    public String submitter_id;
    public Notification notif;

    public Proposal(){

    }

    public Notification getNotif() {
        return notif;
    }

    public void setNotif(Notification notif) {
        this.notif = notif;
    }

    public String getAccepted() {
        return accepted;
    }

    public String getProposal_data() {
        return proposal_data;
    }

    public String getProposal_date() {
        return proposal_date;
    }

    public String getSubmitter_id() {
        return submitter_id;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public void setProposal_data(String proposal_data) {
        this.proposal_data = proposal_data;
    }

    public void setProposal_date(String proposal_date) {
        this.proposal_date = proposal_date;
    }

    public void setSubmitter_id(String submitter_id) {
        this.submitter_id = submitter_id;
    }
}
