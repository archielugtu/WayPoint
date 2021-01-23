package com.springvuegradle.team200.service;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.response.SearchActivityResponse;
import com.springvuegradle.team200.dto.response.SearchUserResponse;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.Location;
import com.springvuegradle.team200.repository.specifications.ActivitySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LocationQueryTest extends AbstractInitialiser {


    public LocationQueryTest() throws ParseException {
        super();

    }

    @Test
    void testFindsNothing () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(1f, 2f, 3f, 4f);
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(0, response.size() );
    }

    @Test
    void testFindsOnlyIlamGardens () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(southPoleLatitude , leftOfIlamGardensLongitude, northPoleLatitude,   betweenIlamAndRiccartonLongitude);
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(1, response.size());

        assertEquals(marathon.getActivityName(), response.get(0).getActivityName());

    }

    @Test
    void testFindsOnlyRiccartonMall () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(southPoleLatitude, betweenIlamAndRiccartonLongitude, northPoleLatitude, rightOfRiccartonMallLongitude  );
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(1, response.size());
        assertEquals(biking.getActivityName(), response.get(0).getActivityName());
    }

    @Test
    void testFindsBothRiccartonMallAndIlamGardens () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds( southPoleLatitude, leftOfIlamGardensLongitude, northPoleLatitude, rightOfRiccartonMallLongitude  );
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(2, response.size());
    }

    @Test
    void testFindNothingNearMeridian () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(southPoleLatitude, atMeridianLeftLongitude,northPoleLatitude , atMeridianRightLongitude );
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(0, response.size());
    }

    @Test
    void testFindAll () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(southPoleLatitude, atMeridianRightLongitude, northPoleLatitude, atMeridianLeftLongitude );
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(5, response.size());
    }

    @Test
    void testFindBothNearMeridian () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(southPoleLatitude, closeToMeridianLeftLongitude, northPoleLatitude, closeToMeridianRightLongitude );
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(2, response.size());
    }

    @Test
    void testFindNothingFromLowLatitude () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(southPoleLatitude, atMeridianRightLongitude, -80f, atMeridianLeftLongitude );
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(0, response.size());
    }

    @Test
    void testFindNothingFromHighLatitude () throws Exception {
        Specification<Activity> spec = ActivitySpec.isActivityWithinBounds(70f, atMeridianRightLongitude, northPoleLatitude, atMeridianLeftLongitude );
        List<Activity> response = activityRepository.findAll(spec);
        assertEquals(0, response.size());
    }


}
