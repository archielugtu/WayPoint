package com.springvuegradle.team200.runner;

import com.springvuegradle.team200.dto.response.RestCountriesResponse;
import com.springvuegradle.team200.model.PassportCountry;
import com.springvuegradle.team200.repository.UserPassportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Populates the passport country database with data obtained from restcountries.eu API
 * <p>
 * This class is automatically run during startup
 */
@Component
public class PopulatePassportCountryRunner implements CommandLineRunner {

    private static final ParameterizedTypeReference<List<RestCountriesResponse>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    @Autowired
    private UserPassportRepository passportRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) {
        // Obtain the countries data through the API
        ResponseEntity<List<RestCountriesResponse>> resp = restTemplate.exchange(
                "https://restcountries.eu/rest/v2/all",
                HttpMethod.GET, null,
                RESPONSE_TYPE);

        // Read the response
        List<RestCountriesResponse> countries = resp.getBody();
        if (countries != null) {
            // Insert into database
            for (RestCountriesResponse country : countries) {
                PassportCountry p = passportRepository.getPassportCountryByCode(country.getCountryCode());
                if (p == null) {
                    passportRepository.save(new PassportCountry(
                            country.getCountryCode(),
                            country.getCountryName(),
                            country.getCountryFlag())
                    );
                }
            }
        }
    }
}
