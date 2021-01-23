package com.springvuegradle.team200.dto.response;

import org.springframework.http.MediaType;

public class PhotoDTO {

    private byte[] data;
    private MediaType mediaType;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

}
