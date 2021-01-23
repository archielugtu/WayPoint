package com.springvuegradle.team200;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team200.dto.request.ActivityRequest;
import com.springvuegradle.team200.dto.request.EditActivityRoleRequest;
import com.springvuegradle.team200.dto.request.EditProfileRequest;
import com.springvuegradle.team200.dto.request.SearchActivityRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.ActivityRole;
import com.springvuegradle.team200.model.ActivityVisibility;
import com.springvuegradle.team200.model.Location;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.*;
import com.springvuegradle.team200.service.UserActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Shared class for initialising a test environment
 * Contains test data that might be useful for cucumber
 */
public class AbstractCucumber {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public Date startDate = formatter.parse("2020-11-20T08:00:00+1300");
    public Date endDate = formatter.parse("2020-12-20T08:00:00+1300");
    public Date creationDate = formatter.parse("2020-01-01T08:00:00+1300");
    public Date birthDate = formatter.parse("2001-01-01T08:00:00+1300");

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
    public PasswordEncoder passwordEncoder;
    @Autowired
    public JwtTokenUtil jwtTokenUtil;
    @Autowired
    public UserActivityService userActivityService;
    @Autowired
    public UserEmailRepository userEmailRepository;
    @Autowired
    public UserActivityHistoryRepository userActivityHistoryRepository;

    public User globalAdmin;

    public List<String> activityTypes = List.of("Air Sphering", "Airboarding");
    public List<String> postHashtags = List.of("#noice", "#leshgo");
    public List<String> putHashtags = List.of("#hot", "#nofilter");
    public Location location = new Location();
    public Location newLocation = new Location();
    public ActivityRequest activityRequest = new ActivityRequest("fun run", "(not actually fun)", activityTypes,
            false, startDate, endDate, location, postHashtags, ActivityVisibility.PUBLIC);
    public ActivityRequest activityEditRequest = new ActivityRequest("Triathlon", "Some more running", activityTypes,
            true, startDate, endDate, newLocation, putHashtags, ActivityVisibility.PUBLIC);
    public EditProfileRequest editProfileRequest = new EditProfileRequest();
    public EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.FOLLOWER);
    public MvcResult latestMockMvcResult;
    public SearchActivityRequest searchActivityRequest;

    public AbstractCucumber() throws ParseException {
    }

}
