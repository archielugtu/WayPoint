package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.OutcomeInputType;
import com.springvuegradle.team200.model.OutcomeQuestion;

import java.util.List;
import java.util.stream.Collectors;

public class ActivityResultSpecResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("question")
    private String question;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("answered")
    private boolean answered;

    @JsonProperty("type")
    private OutcomeInputType inputType;

    @JsonProperty("answers")
    private List<AnswerResponse> answers;

    public static ActivityResultSpecResponse of(OutcomeQuestion question) {
        ActivityResultSpecResponse activityResultSpecResponse = new ActivityResultSpecResponse();
        activityResultSpecResponse.setId(question.getId());
        List<AnswerResponse> answers = question.getActivityResultPossibleAnswers()
                .stream()
                .map(a -> new AnswerResponse(a.getId(), a.getAnswer()))
                .collect(Collectors.toList());
        activityResultSpecResponse.setAnswers(answers);
        activityResultSpecResponse.setQuestion(question.getQuestion());
        activityResultSpecResponse.setUnit(question.getUnit());
        activityResultSpecResponse.setType(question.getInputType());
        return activityResultSpecResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public OutcomeInputType getType() {
        return inputType;
    }

    public void setType(OutcomeInputType inputType) {
        this.inputType = inputType;
    }

    public List<AnswerResponse> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerResponse> answers) {
        this.answers = answers;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
