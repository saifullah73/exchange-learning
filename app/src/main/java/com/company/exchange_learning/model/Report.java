package com.company.exchange_learning.model;

public class Report {
    private String submitter_id;
    private String reporter_id;
    private String post_id;

    public Report(String submitter_id, String reporter_id, String post_id) {
        this.submitter_id = submitter_id;
        this.reporter_id = reporter_id;
        this.post_id = post_id;
    }

    public String getSubmitter_id() {
        return submitter_id;
    }

    public String getReporter_id() {
        return reporter_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setSubmitter_id(String submitter_id) {
        this.submitter_id = submitter_id;
    }

    public void setReporter_id(String reporter_id) {
        this.reporter_id = reporter_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
