package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Entity for each possible answer in a multichoice question
 */
@Entity
@Table(name = "outcome_multichoice_option")
public class OutcomeMultichoiceOption {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "possible_answer_id")
    private Long id;

    @JsonProperty("answer")
    // Answers are always stored as strings - numbers are never parsed
    private String answer;

    @ManyToOne
    @JoinColumn(name = "spec_id")
    private OutcomeQuestion outcomeQuestion;

    public OutcomeMultichoiceOption() {
    }

    public OutcomeMultichoiceOption(String answer) {
        this.answer = answer;
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

    public OutcomeQuestion getOutcomeQuestion() {
        return outcomeQuestion;
    }

    public void setOutcomeQuestion(OutcomeQuestion outcomeQuestion) {
        this.outcomeQuestion = outcomeQuestion;
    }
}
