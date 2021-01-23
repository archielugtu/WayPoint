package com.springvuegradle.team200.service;

import com.springvuegradle.team200.exception.PassportCountryNotFoundException;
import com.springvuegradle.team200.exception.UserNotFoundException;
import com.springvuegradle.team200.model.PassportCountry;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserPassportRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserPassportService {

    @Autowired
    UserPassportRepository userPassportRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Updates the country passports belonging to a user
     *
     * @param userId      User's unique ID
     * @param passports   List of passports to be added to a user
     */
    public void update(Long userId, List<String> passports) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<PassportCountry> countries = new ArrayList<>();
        for (String passport : passports) {
            List<PassportCountry> passportCountriesByName = userPassportRepository.getPassportCountriesByName(passport);
            countries.addAll(passportCountriesByName);
        }
        user.setPassports(countries);
    }

    /**
     * Verifies and retrieves the Passport Country in the
     * database by country name and puts it into a list
     *
     * @param passports List of country names
     * @return  List of Passport Country that's retrieved by country names
     */
    public List<PassportCountry> verifyAndRetrievePassportByCountry(List<String> passports) {
        List<PassportCountry> passportCountries = new ArrayList<>();
        for (String passport : passports) {
            PassportCountry byName = userPassportRepository.findByName(passport);
            if (byName == null) {
                throw new PassportCountryNotFoundException(passport);
            } else {
                passportCountries.add(byName);
            }
        }
        return passportCountries;
    }
}
