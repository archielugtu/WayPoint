package com.springvuegradle.team200.common;

import com.springvuegradle.team200.dto.request.PhotoRequest;
import com.springvuegradle.team200.exception.InvalidImageSizeException;
import com.springvuegradle.team200.exception.NullIdException;
import com.springvuegradle.team200.exception.UnsupportedImageTypeException;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;

public class PhotoUtils {

    // Image is bigger than what it actually is since it is converted to WEBP when cropped
    private static final int MAX_IMAGE_SIZE =  10 * 1000 * 1000; // 10 MB

    /**
     * Verifies whether a photo request is valid
     * if not, an exception is thrown
     *
     * @param request photo request
     */
    public static void verifyPhotoRequest(PhotoRequest request) {
        if (null == request.getActivityId() && null == request.getUserId()) {
            throw new NullIdException();
        } else if (request.getImage().length == 0) {
            throw new InvalidImageSizeException(0);
        } else if (request.getImage().length > MAX_IMAGE_SIZE) {
            throw new InvalidImageSizeException(request.getImage().length);
        }
    }

    /**
     * Verifies whether the media type of the image is in the whitelist of media types
     * Returns the extension of the file if the media type is valid
     *
     * @param mediaType Media type of the image
     * @return Extension of the media type (e.g. 'jpg')
     */
    public static String verifyMediaType(MediaType mediaType) {
        String imageType;
        if (MediaType.IMAGE_GIF.equals(mediaType)) {
            imageType = "image/gif";
        } else if (MediaType.IMAGE_JPEG.equals(mediaType)) {
            imageType = "image/jpeg";
        } else if (MediaType.IMAGE_PNG.equals(mediaType)) {
            imageType = "image/png";
        } else {
            throw new UnsupportedImageTypeException(mediaType);
        }
        return imageType;
    }

    public static byte[] retrieveDefaultImage() throws IOException {
        String photoPath = String.format("%s", "activity-default.jpg");
        ClassLoader classLoader = PhotoUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(photoPath);
        return inputStream.readAllBytes();
    }

    public static byte[] retrieveDefaultBannerImage() throws IOException {
        String photoPath = String.format("%s", "user-banner-default.jpg");
        ClassLoader classLoader = PhotoUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(photoPath);
        return inputStream.readAllBytes();
    }

    public static byte[] retrieveDefaultHeroImage() throws IOException {
        String photoPath = String.format("%s", "profile-default.png");
        ClassLoader classLoader = PhotoUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(photoPath);
        return inputStream.readAllBytes();
    }

}
