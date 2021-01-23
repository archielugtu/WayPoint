package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.request.OutcomeAnswerJson;
import com.springvuegradle.team200.dto.request.UserOutcomeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityOutcomeServiceTest extends AbstractInitialiser {

    public ActivityOutcomeServiceTest() throws ParseException {
        super();
    }

    @Test
    void testEditOutcomeShouldSave() {
        int initialCount = outcomeUserAnswerRepository.findAll().size();
        UserOutcomeRequest request = new UserOutcomeRequest(List.of(new OutcomeAnswerJson(textQuestion.getId(), List.of("yes"))));
        outcomeService.editOutcomeUserResponse(request, marathon.getId(), participantUser.getId());
        assertEquals(initialCount + 1, outcomeUserAnswerRepository.findAll().size());
    }

    @Test
    void testGetShouldMatchPut() {
        UserOutcomeRequest input = new UserOutcomeRequest(List.of(new OutcomeAnswerJson(textQuestion.getId(), List.of("yes"))));
        outcomeService.editOutcomeUserResponse(input, marathon.getId(), participantUser.getId());

        var output = outcomeService.getOutcomeUserResponses(marathon.getId(), participantUser.getId());
        var inputAnswer = input.getAnswers().get(0);
        var outputAnswer = output.getAnswers().get(0);
        assertEquals(inputAnswer.getAnswers().get(0), outputAnswer.getAnswers().get(0));
        assertEquals(inputAnswer.getQuestionId(), outputAnswer.getQuestionId());
    }

    @Test
    void testEditOutcomeShouldRemove() {
        int initialCount = outcomeUserAnswerRepository.findAll().size();
        UserOutcomeRequest addrequest = new UserOutcomeRequest(List.of(new OutcomeAnswerJson(textQuestion.getId(), List.of("yes"))));
        outcomeService.editOutcomeUserResponse(addrequest, marathon.getId(), participantUser.getId());
        assertEquals(initialCount + 1, outcomeUserAnswerRepository.findAll().size());

        UserOutcomeRequest removeRequest = new UserOutcomeRequest(List.of());
        outcomeService.editOutcomeUserResponse(removeRequest, marathon.getId(), participantUser.getId());
        assertEquals(initialCount, outcomeUserAnswerRepository.findAll().size());
    }


}
