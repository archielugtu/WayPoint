package com.springvuegradle.team200.service;

import com.springvuegradle.team200.dto.request.OutcomeAnswerJson;
import com.springvuegradle.team200.dto.request.UserOutcomeRequest;
import com.springvuegradle.team200.exception.*;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles CRUD logic associated with users' activities
 */
@Service
public class OutcomeService {

    private static final String DATE_PATTERN = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
    private static final String TIME_PATTERN = "^\\d{2}:\\d{2}$";
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserActivityHistoryRepository userActivityHistoryRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ActivityTypesRepository activityTypesRepository;
    @Autowired
    HashtagRepository hashtagRepository;
    @Autowired
    OutcomeUserAnswerRepository outcomeUserAnswerRepository;
    @Autowired
    OutcomeQuestionRepository outcomeQuestionRepository;
    @Autowired
    OutcomeMultichoiceOptionRepository outcomeMultichoiceOptionRepository;

    /**
     * sets the user outcome responses to an activity to the ones in the given request
     * if a question is unanswered in the new response, any old instances of answers to that
     * question are removed
     *
     * @param outcomeRequest json request sent from frontend
     * @param activityId     id of the activituy to have the questions answered
     * @param userId         id of the user whose responses are being edited
     */
    @Transactional
    public void editOutcomeUserResponse(UserOutcomeRequest outcomeRequest, Long activityId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        activityRepository.findById(activityId).orElseThrow(() -> new ActivityNotFoundException(userId));

        // remove all existing responses between a user and an activity
        var existingResponses = outcomeUserAnswerRepository.findOutcomeUserAnswerByUserAndActivity(userId, activityId);
        outcomeUserAnswerRepository.deleteAll(existingResponses);

        // Add new answers back in
        var newResponses = outcomeRequest.getAnswers();
        for (var newResponse : newResponses) {
            var question = outcomeQuestionRepository.findById(newResponse.getQuestionId())
                    .orElseThrow(() -> new OutcomeQuestionNotFoundException(userId));
            var responseString = validateAndStringifyResponse(newResponse.getAnswers(), question);
            outcomeUserAnswerRepository.save(new OutcomeUserAnswer(question, user, responseString));
        }

    }

    /**
     * Gets the outcome answers by the user for the given activity
     *
     * @param activityId id of the activity
     * @param userId     id of the user
     * @return user responses to all questions for an activity
     */
    @Transactional
    public UserOutcomeRequest getOutcomeUserResponses(Long activityId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        activityRepository.findById(activityId).orElseThrow(() -> new ActivityNotFoundException(userId));

        var responses = outcomeUserAnswerRepository.findOutcomeUserAnswerByUserAndActivity(userId, activityId);
        var outputResponses = new ArrayList<OutcomeAnswerJson>();

        for (var response : responses) {
            var question = outcomeQuestionRepository.findById(response.getOutcomeQuestion().getId())
                    .orElseThrow(() -> new OutcomeQuestionNotFoundException(userId));
            var answers = parseAnswers(response.getResponse(), question);
            outputResponses.add(new OutcomeAnswerJson(question.getId(), answers));
        }
        return new UserOutcomeRequest(outputResponses);
    }

    /**
     * Validates the string outcomeAnswers and returns them as a string that can be stored in the database
     *
     * @param answers  list of answer strings. Formatting of strings depends on inputType
     * @param question the question which is being answered
     * @return string version of user's answer that can be stored in db
     */
    public String validateAndStringifyResponse(List<String> answers, OutcomeQuestion question) {
        // Multichoice questions must have their answer validated per each OutcomeMultichoiceOption
        Long multichoiceOptionId;
        switch (question.getInputType()) {
            case MULTICHOICE_SINGLE:
                multichoiceOptionId = Long.valueOf(answers.get(0));
                if (!outcomeMultichoiceOptionRepository.existsById(multichoiceOptionId)) {
                    throw new OutcomeMultichoiceOptionNotFoundException(multichoiceOptionId);
                }
                return answers.get(0);
            case MULTICHOICE_COMBINATION:
                for (var choice : answers) {
                    multichoiceOptionId = Long.valueOf(choice);
                    if (!outcomeMultichoiceOptionRepository.existsById(multichoiceOptionId)) {
                        throw new OutcomeMultichoiceOptionNotFoundException(multichoiceOptionId);
                    }
                }
                return String.join(",", answers);
            case DATE:
                Pattern datePattern = Pattern.compile(DATE_PATTERN);
                Matcher dateMatcher = datePattern.matcher(answers.get(0));
                if (!dateMatcher.matches()) {
                    throw new InvalidAnswerException(answers.get(0));
                }
                return answers.get(0);
            case TIME:
                Pattern timePattern = Pattern.compile(TIME_PATTERN);
                Matcher timeMatcher = timePattern.matcher(answers.get(0));
                if (!timeMatcher.matches()) {
                    throw new InvalidAnswerException(answers.get(0));
                }
                return answers.get(0);
            case INTEGER:
                Integer.valueOf(answers.get(0));
                return answers.get(0);
            case FLOAT:
                Float.valueOf(answers.get(0));
                return answers.get(0);
            case CHECKBOX:
                if (answers.get(0).equals("true") || answers.get(0).equals("false")) return answers.get(0);
                throw new InvalidAnswerException(answers.get(0));
            default:
                return answers.get(0);
        }
    }

    /**
     * Parses the answer from it's database stringified version into an array of answer strings
     * Multi-choice combination answers will return an array of all selected, while all others will have array length 1
     *
     * @param answers  string version of answer response to a question
     * @param question the question which is being answered
     * @return Array of string answers parsed for the given question type
     */
    private List<String> parseAnswers(String answers, OutcomeQuestion question) {
        if (question.getInputType() == OutcomeInputType.MULTICHOICE_COMBINATION) {
            return Arrays.asList(answers.split(","));
        }
        return List.of(answers);
    }
}
