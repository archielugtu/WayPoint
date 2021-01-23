package com.springvuegradle.team200.dto.response;

import com.springvuegradle.team200.model.Photo;

public class SinglePhotoResponse {
    private Long id;
    private boolean primary;

    public static SinglePhotoResponse of(Photo photo) {
        SinglePhotoResponse response = new SinglePhotoResponse();
        response.setId(photo.getId());
        response.setPrimary(photo.isPrimary());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
