package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.request.OutcomeAnswerJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import java.text.ParseException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OutcomeTest extends AbstractInitialiser {
    public OutcomeTest() throws ParseException {
        super();
    }

    @Test
    void testValidPutAnswersShouldOk() throws Exception {
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testValidInt() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(intQuestion.getId(), List.of("123"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidInt() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(intQuestion.getId(), List.of("123.4"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidFloat() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(floatQuestion.getId(), List.of("123.456"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidFloat() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(floatQuestion.getId(), List.of("123.4lol xd"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidBoolean() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(booleanQuestion.getId(), List.of("true"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidBoolean() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(booleanQuestion.getId(), List.of("yeeeeeeeeee"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidDate() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(dateQuestion.getId(), List.of("1999-10-7"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidDate() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(dateQuestion.getId(), List.of("wanna go on a date? ;)"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidMultichoiceSingle() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(multiSingleQuestion.getId(), List.of(multiSingleOptionA.getId().toString()))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidMultichoiceSingle() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(multiComboQuestion.getId(), List.of("A"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testValidMultichoiceCombo() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(multiComboQuestion.getId(), List.of(multiComboOptionA.getId().toString(), multiComboOptionB.getId().toString()))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidMultichoiceComboWithOptionFromOtherQuestion() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(multiComboQuestion.getId(), List.of(multiComboQuestion.getId().toString()))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidMultichoiceCombo() throws Exception {

        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(multiSingleQuestion.getId(), List.of("It's time to d-d-d-d-DUEL"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidTimeMidnight() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(timeQuestion.getId(), List.of("00:01"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testValidTime() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(timeQuestion.getId(), List.of("23:59"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testInvalidTime() throws Exception {
        userOutcomeRequest.setAnswers(List.of(new OutcomeAnswerJson(timeQuestion.getId(), List.of("It's time to d-d-d-d-DUEL"))));
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", marathon.getId(), participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testPutAnswersToNonExistingActivityShouldBadRequest() throws Exception {
        mockMvc.perform(put(String.format("/activity/%d/outcomes/%d/answers", 99999999, participantUser.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userOutcomeRequest)))
                .andExpect(status().isBadRequest());
    }
}
