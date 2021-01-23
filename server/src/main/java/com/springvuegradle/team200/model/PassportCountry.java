package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Set;

/**
 * A class to represent an email address belonging to a {@link User}.
 */
@Entity
@Table(name = "passport_country")
public class PassportCountry {

    /**
     * The passport country' unique ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long countryId;

    /**
     * The {@link User} associated with this user
     */
    @ManyToMany(mappedBy = "passports")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<User> users;

    /**
     * Country's unique 3 letter code
     */
    @Column(unique = true)
    private String code;

    @Column(unique = true)
    private String flag;

    /**
     * Country name
     */
    @Column(unique = true)
    private String name;

    public PassportCountry(String code, String name, String flag) {
        this.code = code;
        this.name = name;
        this.flag = flag;
    }

    public PassportCountry() {
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}