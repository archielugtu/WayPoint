package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.exception.CoverImageNotFoundException;
import com.springvuegradle.team200.exception.HeroImageNotFoundException;
import com.springvuegradle.team200.exception.InvalidImageSizeException;
import com.springvuegradle.team200.exception.UnsupportedImageTypeException;
import com.springvuegradle.team200.model.Photo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserImageServiceTest extends AbstractInitialiser {

    @Autowired
    UserImageService service;

    public UserImageServiceTest() throws ParseException {
        super();
    }

    @Test
    void testSaveUserJPGShouldSucceed() {
        photoRequest.setUserId(noneUser.getId());
        photoRequest.setActivityId(null);
        photoRequest.setMediaType(MediaType.IMAGE_JPEG);
        service.update(photoRequest);
    }

    @Test
    void testSaveUserPNGShouldSucceed() {

        photoRequest.setUserId(noneUser.getId());
        photoRequest.setActivityId(null);
        photoRequest.setMediaType(MediaType.IMAGE_PNG);
        service.update(photoRequest);
    }

    @Test
    void testSaveUserGIFShouldSucceed() {

        photoRequest.setUserId(noneUser.getId());
        photoRequest.setActivityId(null);
        photoRequest.setMediaType(MediaType.IMAGE_GIF);
        service.update(photoRequest);
    }

    @Test
    void testSaveUserOtherTypeShouldFail() {
        try {

            photoRequest.setUserId(noneUser.getId());
            photoRequest.setActivityId(null);
            photoRequest.setMediaType(MediaType.APPLICATION_JSON);
            service.update(photoRequest);
            fail(); // should not reach here
        } catch (UnsupportedImageTypeException e) {
        }
    }

    @Test
    void testReadSavedActivityImage() throws IOException {
        photoRequest.setUserId(noneUser.getId());
        photoRequest.setActivityId(null);
        service.update(photoRequest);
        byte[] bytes = service.readHeroImage(noneUser.getId());
        assertTrue(Arrays.equals(bytes, photoRequest.getImage()));
    }

    @Test
    void testSaveCoverImageWithSizeLargerThanAllowedShouldFail() {
        try {
            photoRequest.setUserId(noneUser.getId());
            photoRequest.setActivityId(null);
            photoRequest.setImage("LargeImage".repeat(10000000).getBytes());
            // Test for cover image
            photoRequest.setPrimary(false);
            service.update(photoRequest);

            fail(); // should not reach here
        } catch (InvalidImageSizeException e) { }
    }

    @Test
    void testSavePrimaryImageWithSizeLargerThanAllowedShouldFail() {
        try {
            photoRequest.setUserId(noneUser.getId());
            photoRequest.setActivityId(null);
            photoRequest.setImage("LargeImage".repeat(10000000).getBytes());
            // Test for primary image
            photoRequest.setPrimary(true);
            service.update(photoRequest);
            fail(); // should not reach here
        } catch (InvalidImageSizeException e) { }
    }

}
