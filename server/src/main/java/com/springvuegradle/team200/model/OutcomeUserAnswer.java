package com.springvuegradle.team200.model;


import javax.persistence.*;

/**
 * Represents a users response to a given question
 */
@Entity
@Table(name = "outcome_user_answer")
public class OutcomeUserAnswer {

    /**
     * The activity result specification's unique ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "outcome_user_response_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "spec_id")
    private OutcomeQuestion outcomeQuestion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * responses are stored as strings and parsed when necessary
     */
    @Column
    private String response;

    public OutcomeUserAnswer(OutcomeQuestion outcomeQuestion, User user, String response) {
        this.outcomeQuestion = outcomeQuestion;
        this.user = user;
        this.response = response;
    }

    public OutcomeUserAnswer() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OutcomeQuestion getOutcomeQuestion() {
        return outcomeQuestion;
    }

    public void setOutcomeQuestion(OutcomeQuestion outcomeQuestion) {
        this.outcomeQuestion = outcomeQuestion;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
