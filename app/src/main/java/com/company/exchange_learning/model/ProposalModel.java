package com.company.exchange_learning.model;

public class ProposalModel {
    private String proposal_data;
    private String proposal_date;
    private String submitter_id;

    public ProposalModel(String proposal_data, String proposal_date, String submitter_id) {
        this.proposal_data = proposal_data;
        this.proposal_date = proposal_date;
        this.submitter_id = submitter_id;
    }

    public ProposalModel() {
    }

    public String getProposalData() {
        return proposal_data;
    }

    public void setProposalData(String proposal_data) {
        this.proposal_data = proposal_data;
    }

    public String getProposalDate() {
        return proposal_date;
    }

    public void setProposalDate(String proposal_date) {
        this.proposal_date = proposal_date;
    }

    public String getSubmitterId() {
        return submitter_id;
    }

    public void setSubmitterId(String submitter_id) {
        this.submitter_id = submitter_id;
    }
}
