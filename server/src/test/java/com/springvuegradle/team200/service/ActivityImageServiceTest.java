package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.response.PhotoDTO;
import com.springvuegradle.team200.dto.response.PhotoResponse;
import com.springvuegradle.team200.dto.response.SinglePhotoResponse;
import com.springvuegradle.team200.exception.ImageNotFoundException;
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

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityImageServiceTest extends AbstractInitialiser {

    @Autowired
    ActivityImageService service;

    public ActivityImageServiceTest() throws ParseException {
        super();
    }

    @Test
    void testSaveActivityJPGShouldSucceed() {
        photoRequest.setMediaType(MediaType.IMAGE_JPEG);
        service.create(photoRequest);
    }

    @Test
    void testSaveActivityPNGShouldSucceed() {
        photoRequest.setMediaType(MediaType.IMAGE_PNG);
        service.create(photoRequest);
    }

    @Test
    void testSaveActivityGIFShouldSucceed() {
        photoRequest.setMediaType(MediaType.IMAGE_GIF);
        service.create(photoRequest);
    }

    @Test
    void testSaveImageWithSizeLargerThanAllowedShouldFail() {
        try {
            photoRequest.setImage("LargeImage".repeat(10000000).getBytes());
            service.create(photoRequest);
            fail(); // should not reach here
        } catch (InvalidImageSizeException e) {
        }
    }

    @Test
    void testSaveActivityOtherTypeShouldFail() {
        try {
            photoRequest.setMediaType(MediaType.APPLICATION_JSON);
            service.create(photoRequest);
            fail(); // should not reach here
        } catch (UnsupportedImageTypeException e) {
        }
    }

    @Test
    void testReadSavedActivityImage() {
        Photo photo = service.create(photoRequest);
        List<Photo> images = service.read(marathon.getId());
        assertEquals(photo.getFilename(), images.get(0).getFilename());
    }

    @Test
    void testUpdatePrimaryStatus() {
        Photo photo = service.create(photoRequest);
        service.update(marathon.getId(), photo.getId());

        List<Photo> images = service.read(marathon.getId());
        assertTrue(images.get(0).isPrimary());
    }

    @Test
    void testUpdatePrimaryStatusMultipleImages() {
        service.create(photoRequest);
        service.create(photoRequest);
        Photo photo = service.create(photoRequest);

        service.update(marathon.getId(), photo.getId());

        List<Photo> images = service.read(marathon.getId());
        assertFalse(images.get(0).isPrimary());
        assertFalse(images.get(1).isPrimary());
        assertTrue(images.get(2).isPrimary());
    }

    @Test
    void testReadSaveActivityBinaryData() {
        Photo photo = service.create(photoRequest);
        PhotoDTO photoDTO = service.readBinary(marathon.getId(), photo.getId());
        assertTrue(Arrays.equals(photoDTO.getData(), "fakeImg".getBytes()));
    }

    @Test
    void testReadToDTO() {
        Photo photo = service.create(photoRequest);
        PhotoResponse photoResponse = service.readToDTO(marathon.getId());
        List<SinglePhotoResponse> photos = photoResponse.getPhotos();

        assertEquals(photo.getId(), photos.get(0).getId());
        assertEquals(photo.isPrimary(), photos.get(0).isPrimary());
    }

    @Test
    void testDeleteSavedActivityImage() {
        Photo photo = service.create(photoRequest);
        service.delete(marathon.getId(), photo.getId());

        List<Photo> images = service.read(marathon.getId());
        assertEquals(0, images.size());
    }

    @Test
    void testDeleteNonExistingActivityImageShouldFail() {
        Photo photo = service.create(photoRequest);

        try {
            service.delete(biking.getId(), photo.getId());
            fail(); // should not reach here
        } catch (ImageNotFoundException e) {
        }
    }
}
