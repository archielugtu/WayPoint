package com.springvuegradle.team200.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent an outcome question where users can log the details of their participation.
 */
@Entity
@Table(name = "outcome_question")
public class OutcomeQuestion {


    /**
     * The activity result specification's unique ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "spec_id")
    private Long id;

    @Column
    private String question;

    @Enumerated(EnumType.STRING)
    @JsonProperty("type")
    private OutcomeInputType inputType;

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("answers")
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "outcomeQuestion")
    private List<OutcomeMultichoiceOption> activityResultPossibleAnswers = new ArrayList<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    public OutcomeQuestion(String question, OutcomeInputType inputType, Activity activity) {
        this.question = question;
        this.inputType = inputType;
        this.activity = activity;
    }

    public OutcomeQuestion() {}

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

    public OutcomeInputType getInputType() {
        return inputType;
    }

    public void setInputType(OutcomeInputType inputType) {
        this.inputType = inputType;
    }

    public List<OutcomeMultichoiceOption> getActivityResultPossibleAnswers() {
        return activityResultPossibleAnswers;
    }

    public void setActivityResultPossibleAnswers(List<OutcomeMultichoiceOption> activityResultPossibleAnswers) {
        this.activityResultPossibleAnswers = activityResultPossibleAnswers;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
