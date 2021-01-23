package com.springvuegradle.team200;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team200.dto.request.*;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.*;
import com.springvuegradle.team200.service.ActivityHistoryService;
import com.springvuegradle.team200.service.OutcomeService;
import com.springvuegradle.team200.service.UserActivityService;
import io.cucumber.java.en_old.Ac;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Shared class for initialising a test environment
 * <p>
 * See ActivityServiceTest {@link com.springvuegradle.team200.service.ActivityServiceTest}  for example integration
 */
public class AbstractInitialiser {


    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public Date startDate = formatter.parse("2020-11-20T08:00:00+1300");
    public Date endDate = formatter.parse("2020-12-20T08:00:00+1300");
    public Date creationDate = formatter.parse("2020-01-01T08:00:00+1300");

    @Autowired
    public MockMvc mockMvc;
    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public ActivityRepository activityRepository;
    @Autowired
    public HashtagRepository hashtagRepository;
    @Autowired
    public ActivityTypesRepository activityTypesRepository;
    @Autowired
    public UserActivityHistoryRepository userActivityHistoryRepository;
    @Autowired
    public UserEmailRepository userEmailRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public JwtTokenUtil jwtTokenUtil;
    @Autowired
    public UserActivityService userActivityService;
    @Autowired
    public ActivityHistoryService activityHistoryService;
    @Autowired
    private JwtTokenUtil tokenUtil;
    @Autowired
    public OutcomeService outcomeService;
    @Autowired
    public OutcomeUserAnswerRepository outcomeUserAnswerRepository;
    @Autowired
    public OutcomeQuestionRepository outcomeQuestionRepository;
    @Autowired
    public OutcomeMultichoiceOptionRepository outcomeMultichoiceOptionRepository;
    @Value("${imagesDir}")
    private String imagesDir;
    @Autowired
    public PhotoRepository photoRepository;

    public User noneUser;
    public User followerUser;
    public User participantUser;
    public User organiserUser;
    public User creatorUser;
    public String creatorToken;
    public String participatorToken;
    public String organiserToken;
    public String adminToken;
    public String noneToken;
    public User admin;
    public User globalAdmin;

    public Activity marathon;
    public Activity biking;
    public Activity treePlanting;
    private Activity leftOfMeridianActivity;
    private Activity rightOfMeridianActivity;

    public UserOutcomeRequest userOutcomeRequest;
    public OutcomeQuestion textQuestion;
    public OutcomeQuestion booleanQuestion;
    public OutcomeQuestion floatQuestion;
    public OutcomeQuestion intQuestion;
    public OutcomeQuestion dateQuestion;
    public OutcomeQuestion timeQuestion;
    public OutcomeQuestion multiComboQuestion;
    public OutcomeQuestion multiSingleQuestion;

    public OutcomeMultichoiceOption multiComboOptionA;
    public OutcomeMultichoiceOption multiComboOptionB;
    public OutcomeMultichoiceOption multiSingleOptionA;
    public OutcomeMultichoiceOption multiSingleOptionB;

    public Location location;
    public Location newLocation;

    public Location ilamGardens = new Location();
    public Location riccartonMall = new Location();
    public Location leftOfMeridian = new Location();
    public Location rightOfMeridian = new Location();

    public Float atMeridianLeftLongitude = 179.9f;
    public Float atMeridianRightLongitude = -179.9f;

    public Float closeToMeridianLeftLongitude = 175.f;
    public Float closeToMeridianRightLongitude = -175.f;

    public Float leftOfIlamGardensLongitude = 172.568572f;
    public Float betweenIlamAndRiccartonLongitude = 172.588743f;
    public Float  rightOfRiccartonMallLongitude= 172.608655f;

    public Float northPoleLatitude = 89.9f;
    public Float southPoleLatitude = -89.9f;

    public List<String> activityTypes = List.of("Air Sphering", "Airboarding");
    public List<String> postHashtags = List.of("#noice", "#leshgo");
    public List<String> putHashtags = List.of("#hot", "#nofilter");
    public ActivityRequest activityRequest;
    public ActivityRequest activityEditRequest;
    public MvcResult latestMockMvcResult;
    public ActivityVisibilityRequest activityVisibilityRequest = new ActivityVisibilityRequest();
    public SearchActivityRequest searchActivityRequest;

    public PhotoRequest photoRequest = new PhotoRequest();

    public AbstractInitialiser() throws ParseException {
    }

    @BeforeEach
    void setup() {
        photoRepository.deleteAll();
        outcomeUserAnswerRepository.deleteAll();
        outcomeMultichoiceOptionRepository.deleteAll();
        outcomeQuestionRepository.deleteAll();
        userActivityHistoryRepository.deleteAll();
        userEmailRepository.deleteAll();
        activityRepository.deleteAll();
        userRepository.deleteAll();
        hashtagRepository.deleteAll();

        noneUser = new User("noneUser", passwordEncoder.encode("password1"));
        followerUser = new User("followerUser", passwordEncoder.encode("password1"));
        participantUser = new User("participantUser", passwordEncoder.encode("password1"));
        organiserUser = new User("organiserUser", passwordEncoder.encode("password1"));
        creatorUser = new User("creatorUser", passwordEncoder.encode("password1"));
        admin = new User("Admin", passwordEncoder.encode("passwnnord12"));
        globalAdmin = userRepository.findByIsGlobalAdminTrue();
        admin.setIsAdmin(true);
        userRepository.saveAll(List.of(noneUser, followerUser, participantUser, organiserUser, creatorUser, admin));
        creatorToken = tokenUtil.generateToken(creatorUser);
        participatorToken = tokenUtil.generateToken(participantUser);
        organiserToken = tokenUtil.generateToken(organiserUser);
        adminToken = tokenUtil.generateToken(admin);
        noneToken = tokenUtil.generateToken(noneUser);

        ActivityType running = activityTypesRepository.getActivityTypesFromName("Running");
        List<ActivityType> activityActivityTypes = List.of(running);
        Set<Hashtag> startingHashtags = Set.of(new Hashtag("#yes"), new Hashtag("#no"));
        hashtagRepository.saveAll(startingHashtags);

        location = new Location();
        location.setPlaceId("placeId");
        location.setLongitude(0f);
        location.setLatitude(0f);

        newLocation = new Location();
        newLocation.setPlaceId("newPlaceId");

        marathon = new Activity("marathon", "running alot", location, false, startDate, endDate, creationDate,
                ActivityVisibility.PUBLIC);
        activityEditRequest = new ActivityRequest("Triathlon", "Some more running", activityTypes,
                true, startDate, endDate, newLocation, putHashtags, ActivityVisibility.PUBLIC);

        marathon.setHashtags(startingHashtags);
        marathon.setActivityTypes(activityActivityTypes);

        marathon.setCreator(creatorUser);
        marathon.setParticipants(Set.of(participantUser));
        marathon.setOrganisers(Set.of(organiserUser));
        marathon.setFollowers(Set.of(followerUser));

        activityRequest = new ActivityRequest("fun run", "(not actually fun)", activityTypes,
                false, startDate, endDate, location, postHashtags, ActivityVisibility.PUBLIC);

        activityRepository.save(marathon);

        biking = new Activity("marathon", "biking alot", location, false, startDate, endDate, creationDate,
                ActivityVisibility.PUBLIC);

        biking.setCreator(creatorUser);
        biking.setParticipants(Set.of(participantUser));
        biking.setOrganisers(Set.of(organiserUser));
        biking.setFollowers(Set.of(followerUser));

        activityRepository.save(biking);

        treePlanting = new Activity("tree planting", "we need more trees", location, true, null, null, creationDate,
                ActivityVisibility.PUBLIC);

        activityRepository.save(treePlanting);

        leftOfMeridianActivity = new Activity();
        rightOfMeridianActivity = new Activity();

        ilamGardens.setLatitude(-43.522386f);
        ilamGardens.setLongitude(172.577221f);
        riccartonMall.setLatitude(-43.530330f);
        riccartonMall.setLongitude(172.598410f);
        leftOfMeridian.setLatitude(-60.f);
        leftOfMeridian.setLongitude(178.f);
        rightOfMeridian.setLatitude(-60.f);
        rightOfMeridian.setLongitude(-178.f);

        marathon.setLocation(ilamGardens);
        biking.setLocation(riccartonMall);
        leftOfMeridianActivity.setLocation(leftOfMeridian);
        rightOfMeridianActivity.setLocation(rightOfMeridian);

        activityRepository.save(leftOfMeridianActivity);
        activityRepository.save(rightOfMeridianActivity);

        activityRepository.save(marathon);
        activityRepository.save(biking);

        textQuestion = new OutcomeQuestion("Why is there smoke coming out of your oven?", OutcomeInputType.TEXT, marathon);
        booleanQuestion = new OutcomeQuestion("You call hamburgers steamed hams?", OutcomeInputType.CHECKBOX, marathon);
        floatQuestion = new OutcomeQuestion("uh-huh what region?", OutcomeInputType.FLOAT, marathon);
        dateQuestion = new OutcomeQuestion("Really. Well, I'm from Utica, and I've never heard anyone use the phrase \"steamed hams.\"", OutcomeInputType.DATE, marathon);
        intQuestion = new OutcomeQuestion("You know, these hamburgers are quite similar to the ones they have at Krusty Burger.", OutcomeInputType.INTEGER, marathon);
        timeQuestion = new OutcomeQuestion("For steamed hams?", OutcomeInputType.TIME, marathon);
        outcomeQuestionRepository.saveAll(List.of(textQuestion, booleanQuestion, floatQuestion, dateQuestion, intQuestion, timeQuestion));

        multiComboQuestion = new OutcomeQuestion("For steamed hams?", OutcomeInputType.MULTICHOICE_COMBINATION, marathon);
        multiSingleQuestion = new OutcomeQuestion("For steamed hams?", OutcomeInputType.MULTICHOICE_SINGLE, marathon);
        outcomeQuestionRepository.saveAll(List.of(multiComboQuestion, multiSingleQuestion));

        multiComboOptionA = new OutcomeMultichoiceOption("A");
        multiComboOptionB= new OutcomeMultichoiceOption("B");
        multiSingleOptionA= new OutcomeMultichoiceOption("AAAAAAAAAAAAA");
        multiSingleOptionB= new OutcomeMultichoiceOption("BBBBBBBBBBBBBBBB");
        outcomeMultichoiceOptionRepository.saveAll(List.of(multiComboOptionA, multiComboOptionB, multiSingleOptionA, multiSingleOptionB));

        multiComboQuestion.setActivityResultPossibleAnswers(List.of(multiComboOptionA, multiComboOptionB));
        multiSingleQuestion.setActivityResultPossibleAnswers(List.of(multiSingleOptionA, multiSingleOptionB));
        outcomeQuestionRepository.saveAll(List.of(multiComboQuestion, multiSingleQuestion));

        activityRepository.save(marathon);

        outcomeQuestionRepository.saveAll(List.of(textQuestion, booleanQuestion, floatQuestion, dateQuestion, intQuestion, timeQuestion));
        outcomeQuestionRepository.saveAll(List.of(multiComboQuestion, multiSingleQuestion));
        marathon.setOutcomeQuestions(Set.of(textQuestion));

        userOutcomeRequest = new UserOutcomeRequest(List.of(new OutcomeAnswerJson(textQuestion.getId(), List.of("Yes"))));
        searchActivityRequest = new SearchActivityRequest();

        photoRequest.setActivityId(marathon.getId());
        photoRequest.setImage("fakeImg".getBytes());
        photoRequest.setMediaType(MediaType.IMAGE_PNG);
        photoRequest.setPrimary(true);

        // Delete files generated by image tests
        try {
            FileSystemUtils.deleteRecursively(Paths.get(imagesDir));
        } catch (IOException exception) { }

        activityRepository.save(marathon);
    }
}
