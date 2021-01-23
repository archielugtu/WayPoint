package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EditEmailRequest {

    @JsonProperty("primary_email")
    private String primaryEmail;

    @JsonProperty("additional_email")
    private List<String> additionalEmails;

    /**
     * Constructor
     *
     * @param primaryEmail     The String primary email for the user
     * @param additionalEmails A List of additional String emails associated with the user
     */
    public EditEmailRequest(String primaryEmail, List<String> additionalEmails) {
        this.primaryEmail = primaryEmail;
        this.additionalEmails = additionalEmails;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public List<String> getAdditionalEmails() {
        return additionalEmails;
    }
}

