package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestCountriesResponse {
    @JsonProperty("name")
    private String countryName;

    @JsonProperty("alpha3Code")
    private String countryCode;

    @JsonProperty("flag")
    private String countryFlag;

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryFlag() {
        return countryFlag;
    }
}
