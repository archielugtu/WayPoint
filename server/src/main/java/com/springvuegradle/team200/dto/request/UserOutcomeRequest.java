package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserOutcomeRequest {

    @JsonProperty("answers")
    private List<OutcomeAnswerJson> answers;

    /**
     * Constructor
     *
     * @param answers List of {@link OutcomeAnswerJson}
     */
    public UserOutcomeRequest(List<OutcomeAnswerJson> answers) {
        this.answers = answers;
    }

    public UserOutcomeRequest() {

    }

    public List<OutcomeAnswerJson> getAnswers() {
        return answers;
    }

    public void setAnswers(List<OutcomeAnswerJson> answers) {
        this.answers = answers;
    }
}

