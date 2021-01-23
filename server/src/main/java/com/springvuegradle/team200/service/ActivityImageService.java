package com.springvuegradle.team200.service;

import com.springvuegradle.team200.common.PhotoUtils;
import com.springvuegradle.team200.dto.request.PhotoRequest;
import com.springvuegradle.team200.dto.response.PhotoDTO;
import com.springvuegradle.team200.dto.response.PhotoResponse;
import com.springvuegradle.team200.dto.response.SinglePhotoResponse;
import com.springvuegradle.team200.exception.*;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.Photo;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.springvuegradle.team200.common.PhotoUtils.verifyPhotoRequest;

/**
 * Defines functionalities related to CRUD-ing activity photos
 */
@Transactional
@Service
public class ActivityImageService {

    @Value("${imagesDir}")
    private String imagesDir;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PhotoRepository photoRepository;

    /**
     * Creates a single activity photo and saves it to the DB
     * <p>
     * The request object must have:
     * - non-null activity ID
     * - image size of not 0
     * otherwise an exception is thrown
     *
     * @param request request object
     * @return Photo saved to the DB
     */
    public Photo create(PhotoRequest request) {
        PhotoUtils.verifyPhotoRequest(request);
        return create(
                request.getActivityId(),
                request.getImage(),
                request.getMediaType()
        );
    }

    /**
     * Creates a single photo for an activity in the database
     *
     * @param activityId ID of the activity
     * @param image      byte array representation of the image
     * @param mediaType  Media type of the image
     * @return Photo saved to the database
     */
    private Photo create(Long activityId, byte[] image, MediaType mediaType) {
        String imageType = PhotoUtils.verifyMediaType(mediaType);
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        List<Photo> images = activity.getImages();
        String imageName = String.format("%s.%s", UUID.randomUUID().toString(), imageType.split("/")[1]);

        try {
            Files.createDirectories(Paths.get(imagesDir));
            Path path = Paths.get(imagesDir + "/" + imageName);
            Files.write(path, image);

            Photo photo = new Photo();
            photo.setActivity(activity);
            photo.setFilename(imageName);
            photo.setImageType(imageType);
            photo.setPrimary(false);
            images.add(photo);

            photoRepository.save(photo);
            return photo;

        } catch (IOException exception) {
            throw new UnableToSaveImageException(activityId);
        }
    }


    /**
     * Reads all the photos of an activity
     *
     * @param activityId ID of the activity
     * @return List of photos associated to the activity
     */
    public List<Photo> read(Long activityId) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        return activity.getImages()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Reads a activity's photo's binary data and returns it
     *
     * @param activityId ID of the activity
     * @param photoId    ID of the photo
     * @return binary data of the photo
     */
    public PhotoDTO readBinary(Long activityId, Long photoId) {
        Photo photo = photoRepository.findByIdAndActivity_Id(photoId, activityId)
                .orElseThrow(() -> new ImageNotFoundException(photoId));

        try {
            PhotoDTO dto = new PhotoDTO();
            String photoPath = String.format("%s/%s", imagesDir, photo.getFilename());
            dto.setData(Files.readAllBytes(Paths.get(photoPath)));
            dto.setMediaType(MediaType.parseMediaType(photo.getImageType()));
            return dto;
        } catch (IOException exception) {
            throw new ImageNotFoundException(photoId);
        }
    }

    public byte[] readHeroImage(Long activityId) throws IOException {
        Optional<Photo> photo = photoRepository.findByIsPrimaryTrueAndActivity_Id(activityId);
        if (photo.isEmpty()) {
           return PhotoUtils.retrieveDefaultImage();
        }

        try {
            String photoPath = String.format("%s/%s", imagesDir, photo.get().getFilename());
            return Files.readAllBytes(Paths.get(photoPath));
        } catch (IOException exception) {
            return PhotoUtils.retrieveDefaultImage();
        }
    }

    /**
     * Reads all the photos of an activity and turn it into a DTO
     * containing photo's ID and primary status
     *
     * @param activityId ID of the activity
     * @return List of photos associated to the activity
     */
    public PhotoResponse readToDTO(Long activityId) {
        List<SinglePhotoResponse> collect = read(activityId)
                .stream()
                .map(p -> SinglePhotoResponse.of(p))
                .collect(Collectors.toList());

        return new PhotoResponse(collect);
    }

    /**
     * Modifies the primary photo of an activity
     * <p>
     * Removes the primary status of all other photos of an activity and
     * sets the requested photo to be a primary photo
     *
     * @param activityId ID of the activity
     * @param photoId    ID of the photo
     */
    public void update(Long activityId, Long photoId) {
        List<Photo> photos = photoRepository.findByActivity_Id(activityId);
        for (Photo photo : photos) {
            // By default set it to non primary
            photo.setPrimary(false);
            // Except if it is the photo we are interested in making it to be primary
            if (photo.getId().equals(photoId)) {
                photo.setPrimary(true);
            }
        }

        photoRepository.saveAll(photos);
    }

    /**
     * Deletes a photo associated to an activity
     *
     * @param activityId ID of the activity
     * @param photoId    ID of the photo
     */
    public void delete(Long activityId, Long photoId) {
        Photo photo = photoRepository.findByIdAndActivity_Id(photoId, activityId)
                .orElseThrow(() -> new ImageNotFoundException(photoId));

        try {
            Files.delete(Paths.get(imagesDir + "/" + photo.getFilename()));
            photoRepository.delete(photo);
        } catch (IOException exception) {
            throw new UnableToDeleteImageException(photo.getFilename());
        }
    }
}
