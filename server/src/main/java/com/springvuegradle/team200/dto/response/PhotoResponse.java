package com.springvuegradle.team200.dto.response;

import java.util.List;

public class PhotoResponse {
    private List<SinglePhotoResponse> photos;

    public PhotoResponse(List<SinglePhotoResponse> photos) {
        this.photos = photos;
    }

    public List<SinglePhotoResponse> getPhotos() {
        return photos;
    }

    public void setPhotos(List<SinglePhotoResponse> photos) {
        this.photos = photos;
    }
}
