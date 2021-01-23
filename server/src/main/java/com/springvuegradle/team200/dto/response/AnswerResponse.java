package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AnswerResponse {

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("id")
    private Long id;

    public AnswerResponse(Long id, String answer) {
        this.answer = answer;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
