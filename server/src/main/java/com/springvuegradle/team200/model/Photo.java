package com.springvuegradle.team200.model;

import javax.persistence.*;

/**
 * Defines a photo obj for activities
 */
@Entity
@Table(name = "app_photos")
public class Photo {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "filename")
    private String filename;
    @Column(name = "image_type")
    private String imageType;
    @Column(name = "is_primary")
    private boolean isPrimary;
    @ManyToOne
    @JoinColumn(name="activity_id", nullable = true)
    private Activity activity;
    @ManyToOne
    @JoinColumn(name="user_id", nullable = true)
    private User user;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
