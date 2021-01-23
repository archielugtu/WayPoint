package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * A class to represent an email address belonging to a {@link User}.
 */
@Entity
@Table(name = "app_user_email")
public class UserEmail {

    /**
     * The email address' unique ID
     */
    @Id
    @GeneratedValue
    @Column(name = "email_id")
    private Long id;

    /**
     * The {@link User} associated with this email address
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    /**
     * The email address
     */
    @Column(unique = true)
    private String address;

    /**
     * A boolean representing whether this email address is the user's primary email address
     */
    @Column
    private boolean isPrimary;

    public UserEmail() {
    }

    public UserEmail(String address, User user, Boolean isPrimary) {
        this.address = address;
        this.user = user;
        this.isPrimary = isPrimary;
    }

    /**
     * @return The email address' unique Long ID
     */
    public Long getUserEmailID() {
        return id;
    }

    /**
     * @return The {@link User} associated with this email address
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the {@link User} associated with this email address.
     *
     * @param user The new {@link User} associated with this email address.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return The email address, as a String
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return A boolean representing whether this email address is the user's primary email address
     */
    public boolean getIsPrimary() {
        return isPrimary;
    }

    /**
     * Sets whether this email address is the user's primary email address.
     *
     * @param isPrimary A boolean representing whether this email address is the user's primary email address.
     */
    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}