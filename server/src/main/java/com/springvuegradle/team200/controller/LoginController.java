package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.dto.request.LoginRequest;
import com.springvuegradle.team200.dto.response.AuthResponse;
import com.springvuegradle.team200.dto.response.LoginResponse;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserEmailRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class LoginController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserEmailRepository userEmailRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/auth")
    @ResponseBody
    public ResponseEntity<AuthResponse> getAuth() {
        User user = jwtTokenUtil.getRequestingUser();
        if (user == null || !user.isCredentialsNonExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        user = userRepository.getUserFromId(user.getId());
        logger.info("User " + user.getId() + " is requesting auth details");
        return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.of(user));
    }

    /**
     * Generates a cookie containing JWT for a particular user
     *
     * @param user JWT owner user
     * @return Cookie associated with a user containing JWT
     */
    private ResponseCookie generateCookie(User user) {
        final String token = jwtTokenUtil.generateToken(user);
        return ResponseCookie.from("token", token)
                .sameSite("Strict")
                .path("/")
                .httpOnly(true)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        final String email = loginRequest.getEmail();
        final String password = loginRequest.getPassword();

        final User user = userRepository.getUserFromEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("USER NOT FOUND"));
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            String urlRedirect = "homeFeed";
            // Global admin does not have a profile page so just send them to the home page
            if (user.getIsGlobalAdmin()) {
                urlRedirect = "adminDashboard";
            }

            // Frontend router navigates to aliased routes - "profile" is actually /profile/:id
            var loginResponse = new LoginResponse("SUCCESS", urlRedirect, user.getId(), user.getIsGlobalAdmin(), user.getIsAdmin());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, generateCookie(user).toString())
                    .body(loginResponse);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("WRONG PASSWORD"));
        }
    }
}
