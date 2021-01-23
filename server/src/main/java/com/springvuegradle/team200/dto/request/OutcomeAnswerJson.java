package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.OutcomeQuestion;

import java.util.List;

/**
 * Helper item to define a single outcome answer for {@link UserOutcomeRequest}
 */
public class OutcomeAnswerJson {
    @JsonProperty("id")
    private Long questionId;

    @JsonProperty("answer")
    private List<String> answers;


    /**
     * Constructor
     *
     * @param questionId        id of {@link OutcomeQuestion}
     * @param answers           array of answers. size = 1 for non MultiChoice_Combinatoin responses
     *                          Answers are simple strings for most types, but can refer to the
     */
    public OutcomeAnswerJson(Long questionId, List<String> answers) {
        this.questionId = questionId;
        this.answers = answers;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
