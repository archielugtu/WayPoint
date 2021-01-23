package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.dto.request.ActivityRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.repository.UserEmailRepository;
import com.springvuegradle.team200.repository.UserPassportRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
public class DebugController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserEmailRepository userEmailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserPassportRepository passportRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @GetMapping("/Debug/userOrAdmin/{userId}")
    @ResponseBody
    public String userOrAdmin(@PathVariable Long userId) {
        return "THIS ENDPOINT IS ACCESSIBLE TO THE LOGGED IN USER WHOSE userId = {userId} AND TO ANY ADMIN";
    }

    @PreAuthorize("principal.getIsAdmin()")
    @GetMapping("/Debug/adminOnly/{userId}")
    @ResponseBody
    public String adminOnly(@PathVariable Long userId) {
        return "THIS ENDPOINT IS ONLY ACCESSIBLE TO ADMINSN";
    }

    @PreAuthorize("@activityRolesService.hasOrganiserOrGreaterRole(authentication, #activityId) OR principal.getIsAdmin()")
    @GetMapping("/Debug/creatorAndOrganiser/activities/{activityId}")
    @ResponseBody
    public String creatorOrOrganiserOnly(@Valid @RequestBody ActivityRequest activityRequest,
                                         Authentication authentication,
                                         @PathVariable("activityId") Long activityId) {
        return "THIS ENDPOINT IS ONLY ACCESSIBLE TO CREATORS AND ORGANISERS";
    }
    @PreAuthorize("@activityRolesService.hasCorrectRolePerVisibility(authentication, #activityId) OR principal.getIsAdmin()")
    @GetMapping("/Debug/creator/activities/{activityId}")
    @ResponseBody
    public String correctRoleByVisibility(Authentication authentication, @PathVariable("activityId") Long activityId) {
        return "THIS ENDPOINT IS NOT ACCESSIBLE TO THIS USER";
    }
}
