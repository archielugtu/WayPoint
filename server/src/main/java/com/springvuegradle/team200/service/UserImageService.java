package com.springvuegradle.team200.service;

import com.springvuegradle.team200.common.PhotoUtils;
import com.springvuegradle.team200.dto.request.PhotoRequest;
import com.springvuegradle.team200.exception.*;
import com.springvuegradle.team200.model.Photo;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.PhotoRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class UserImageService {

    @Value("${imagesDir}")
    private String imagesDir;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepository photoRepository;

    public Photo update(PhotoRequest request) {
        PhotoUtils.verifyPhotoRequest(request);
        return update(
                request.getUserId(),
                request.getImage(),
                request.getMediaType(),
                request.isPrimary()
        );
    }

    /**
     * Creates a single photo for an activity in the database
     *
     * @param userId ID of the activity
     * @param image      byte array representation of the image
     * @param mediaType  Media type of the image
     * @return Photo saved to the database
     */
    private Photo update(Long userId, byte[] image, MediaType mediaType, boolean isPrimary) {
        String imageType = PhotoUtils.verifyMediaType(mediaType);
        if (isPrimary) {
            photoRepository.deleteByIsPrimaryTrueAndUser_Id(userId);
        } else {
            photoRepository.deleteByIsPrimaryFalseAndUser_Id(userId);
        }

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Photo> images = user.getImages();
        String imageName = String.format("%s.%s", UUID.randomUUID().toString(), imageType.split("/")[1]);

        try {
            Files.createDirectories(Paths.get(imagesDir));
            Path path = Paths.get(imagesDir + "/" + imageName);
            Files.write(path, image);

            Photo photo = new Photo();
            photo.setUser(user);
            photo.setFilename(imageName);
            photo.setImageType(imageType);
            photo.setPrimary(isPrimary);
            images.add(photo);

            photoRepository.save(photo);
            return photo;

        } catch (IOException exception) {
            throw new UnableToSaveImageException(userId);
        }
    }

    public byte[] readHeroImage(Long userId) throws IOException {
        Optional<Photo> photo = photoRepository.findByIsPrimaryTrueAndUser_Id(userId);
        if (photo.isEmpty()) {
            return PhotoUtils.retrieveDefaultHeroImage();
        }
        try {
            String photoPath = String.format("%s/%s", imagesDir, photo.get().getFilename());
            return Files.readAllBytes(Paths.get(photoPath));
        } catch (IOException exception) {
            return PhotoUtils.retrieveDefaultHeroImage();
        }
    }

    public byte[] readCoverImage(Long userId) throws IOException {
        Optional<Photo> photo = photoRepository.findByIsPrimaryFalseAndUser_Id(userId);
        if (photo.isEmpty()) {
            return PhotoUtils.retrieveDefaultBannerImage();
        }

        try {
            String photoPath = String.format("%s/%s", imagesDir, photo.get().getFilename());
            return Files.readAllBytes(Paths.get(photoPath));
        } catch (IOException exception) {
            return PhotoUtils.retrieveDefaultBannerImage();
        }
    }

    /**
     * Deletes a photo associated to an user
     *
     * @param userId ID of the user
     * @param isPrimary True if primary image, else if cover image
     */
    public void delete(Long userId, boolean isPrimary) {
        Optional<Photo> photo = photoRepository.findByIsPrimaryAndUser_Id(isPrimary, userId);
        // They dont have a cover or primary image, stop here
        if (photo.isEmpty()) { return; }

        try {
            Files.delete(Paths.get(imagesDir + "/" + photo.get().getFilename()));
            photoRepository.deleteByIsPrimaryAndUser_Id(isPrimary, userId);
        } catch (IOException exception) {
            throw new UnableToDeleteImageException(photo.get().getFilename());
        }
    }

}
