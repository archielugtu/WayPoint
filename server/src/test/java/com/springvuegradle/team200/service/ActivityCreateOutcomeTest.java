package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.request.ActivityRequest;
import com.springvuegradle.team200.dto.response.UserResponse;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityCreateOutcomeTest extends AbstractInitialiser {

    @Autowired
    UserActivityService userActivityService;
    @Autowired
    OutcomeService outcomeService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ActivityTypesRepository activityTypesRepository;
    @Autowired
    HashtagRepository hashtagRepository;
    @Autowired
    OutcomeQuestionRepository outcomeQuestionRepository;

    private ActivityRequest request;
    private Activity activity;

    private final List<String> hashtags = new ArrayList<>(Collections.singletonList("running"));

    public ActivityCreateOutcomeTest() throws ParseException {
        super();
    }

    private void setupRequestMultichoice(String ans1, String ans2) {
        List<String> activityTypes = new ArrayList<>();
        activityTypes.add("Hiking");

        List<OutcomeMultichoiceOption> possibleAnswers = new ArrayList<>();
        possibleAnswers.add(new OutcomeMultichoiceOption(ans1));
        possibleAnswers.add(new OutcomeMultichoiceOption(ans2));

        request = new ActivityRequest("Triathlon", "Some more running", activityTypes, true, null, null, null, hashtags, ActivityVisibility.PUBLIC);
        OutcomeQuestion textbox = new OutcomeQuestion("WHERE U HIKE?", OutcomeInputType.MULTICHOICE_COMBINATION, null);
        textbox.setActivityResultPossibleAnswers(possibleAnswers);
        Set<OutcomeQuestion> questions = new HashSet<>();
        questions.add(textbox);
        request.setOutcomeQuestions(questions);
    }

    void setupRequestTextQuestion() {
        request = new ActivityRequest("Triathlon", "Some more running", activityTypes, true, null, null, null, hashtags, ActivityVisibility.PUBLIC);
        OutcomeQuestion textbox = new OutcomeQuestion("WHERE U HIKE?", OutcomeInputType.TEXT, null);

        Set<OutcomeQuestion> questions = new HashSet<>();
        questions.add(textbox);

        request.setOutcomeQuestions(questions);
    }

    @AfterEach
    void reset() {
        request = null;
    }

    @Test
    void createActivityWithResultsShouldSaveQuestion() {
        setupRequestTextQuestion();
        activity = userActivityService.create(noneUser.getId(), request);
        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            assertEquals("WHERE U HIKE?", outcomeQuestion.getQuestion());
        }
    }

    @Test
    void createActivityWithResultsShouldSaveInputType() {
        setupRequestTextQuestion();
        activity = userActivityService.create(noneUser.getId(), request);
        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            assertEquals(OutcomeInputType.TEXT, outcomeQuestion.getInputType());
        }
    }

    @Test
    void createActivityWithResultsShouldSavePossibleAnswers() {
        setupRequestMultichoice("Nowhere", "Everywhere");
        activity = userActivityService.create(noneUser.getId(), request);
        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            assertEquals("Nowhere", outcomeQuestion.getActivityResultPossibleAnswers().get(0).getAnswer());
            assertEquals("Everywhere", outcomeQuestion.getActivityResultPossibleAnswers().get(1).getAnswer());

        }
    }

    @Test
    void editActivityWithResultsShouldSavePossibleAnswers() {
        setupRequestTextQuestion();
        activity = userActivityService.create(noneUser.getId(), request);

        // There is only 1 question so it's file to iterate through everything
        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            outcomeQuestion.setInputType(OutcomeInputType.MULTICHOICE_COMBINATION);
            List<OutcomeMultichoiceOption> options = new ArrayList<>();
            options.add(new OutcomeMultichoiceOption("Answer 1"));
            options.add(new OutcomeMultichoiceOption("Answer 2"));
            outcomeQuestion.setActivityResultPossibleAnswers(options);
        }
        request.setOutcomeQuestions(activity.getOutcomeQuestions());
        userActivityService.edit(activity.getId(), noneUser.getId(), request);
        activity = userActivityService.readByActivityId(activity.getId());

        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            assertEquals("Answer 1", outcomeQuestion.getActivityResultPossibleAnswers().get(0).getAnswer());
            assertEquals("Answer 2", outcomeQuestion.getActivityResultPossibleAnswers().get(1).getAnswer());
        }
    }

    @Test
    void editActivityWithResultsShouldSaveQuestion() {
        setupRequestTextQuestion();
        activity = userActivityService.create(noneUser.getId(), request);

        // There is only 1 question so it's file to iterate through everything
        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            outcomeQuestion.setQuestion("My new question");
        }
        request.setOutcomeQuestions(activity.getOutcomeQuestions());
        userActivityService.edit(activity.getId(), noneUser.getId(), request);
        activity = userActivityService.readByActivityId(activity.getId());

        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            assertEquals("My new question", outcomeQuestion.getQuestion());
        }
    }


    @ParameterizedTest
    @EnumSource(value = OutcomeInputType.class)
    void editActivityWithResultsShouldSaveInputType(OutcomeInputType inputType) {
        setupRequestTextQuestion();
        activity = userActivityService.create(noneUser.getId(), request);

        // There is only 1 question so it's file to iterate through everything
        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            outcomeQuestion.setInputType(inputType);
        }
        request.setOutcomeQuestions(activity.getOutcomeQuestions());
        userActivityService.edit(activity.getId(), noneUser.getId(), request);
        activity = userActivityService.readByActivityId(activity.getId());

        for (OutcomeQuestion outcomeQuestion : activity.getOutcomeQuestions()) {
            assertEquals(inputType, outcomeQuestion.getInputType());
        }
    }


    @Test
    void getUserWhoAnsweredQuestionsShouldReturnCorrectUsers() {
        outcomeService.editOutcomeUserResponse(userOutcomeRequest, marathon.getId(), participantUser.getId());
        Set<UserResponse> users = userActivityService.getActivityOutcomeUsers(marathon.getId());
        assertEquals(1, users.size());
    }

}
