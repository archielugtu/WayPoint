package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.response.SearchActivityResponse;
import com.springvuegradle.team200.model.Activity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivitySearchTest extends AbstractInitialiser {

    public ActivitySearchTest() throws ParseException {
        super();
    }

    @Test
    void testSearchWithNoParamsShouldReturnActivitiesVisibleOnMap() {
        // By default the map boundary in search request is set to the whole earth
        Page<SearchActivityResponse> responses = userActivityService.readByQuery(searchActivityRequest, noneUser.getId());
        List<SearchActivityResponse> content = responses.getContent();
        assertTrue(content.size() == 3);
    }

    @Test
    void testByHashtagsWithAndShouldReturnActivitiesContainingHashtags() {
        Activity activity = userActivityService.create(noneUser.getId(), activityRequest);

        searchActivityRequest.setHashtag("#leshgo");
        searchActivityRequest.setHashtagSearchType("and");

        Page<SearchActivityResponse> responses = userActivityService.readByQuery(searchActivityRequest, noneUser.getId());
        List<SearchActivityResponse> content = responses.getContent();
        List<Long> collect = content.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(collect.contains(activity.getId()));
        assertTrue(collect.size() == 1);
    }

    @Test
    void testByHashtagsWithOrShouldReturnActivitiesContainingHashtags() {
        Activity activity = userActivityService.create(noneUser.getId(), activityRequest);

        searchActivityRequest.setHashtag("#leshgo");
        searchActivityRequest.setHashtagSearchType("or");

        Page<SearchActivityResponse> responses = userActivityService.readByQuery(searchActivityRequest, noneUser.getId());
        List<SearchActivityResponse> content = responses.getContent();
        List<Long> collect = content.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(collect.contains(activity.getId()));
        assertTrue(collect.size() == 1);
    }

    @Test
    void testByMultipleHashtagWithAndShouldReturnActivitiesContainingAllHashtags() {
        Activity activity = userActivityService.create(noneUser.getId(), activityRequest);

        searchActivityRequest.setHashtag("#leshgo;#noice");
        searchActivityRequest.setHashtagSearchType("and");

        Page<SearchActivityResponse> responses = userActivityService.readByQuery(searchActivityRequest, noneUser.getId());

        List<SearchActivityResponse> content = responses.getContent();
        List<Long> collect = content.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(collect.contains(activity.getId()));
        assertTrue(collect.size() == 1);
    }

    @Test
    void testByMultipleUnusedHashtagWithAndShouldReturnNoActivities() {
        userActivityService.create(noneUser.getId(), activityRequest);

        searchActivityRequest.setHashtag("#leshgo;#lobster");
        searchActivityRequest.setHashtagSearchType("and");

        Page<SearchActivityResponse> responses = userActivityService.readByQuery(searchActivityRequest, noneUser.getId());
        List<SearchActivityResponse> content = responses.getContent();
        List<Long> collect = content.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(collect.size() == 0);
    }

    @Test
    void testByMultipleHashtagWithOrShouldReturnMatchingActivities() {
        Activity activity = userActivityService.create(noneUser.getId(), activityRequest);

        searchActivityRequest.setHashtag("#leshgo;#lobster");
        searchActivityRequest.setHashtagSearchType("or");

        Page<SearchActivityResponse> responses = userActivityService.readByQuery(searchActivityRequest, noneUser.getId());
        List<SearchActivityResponse> content = responses.getContent();
        List<Long> collect = content.stream().map(a -> a.getId()).collect(Collectors.toList());
        assertTrue(collect.contains(activity.getId()));
        assertTrue(collect.size() == 1);
    }

}