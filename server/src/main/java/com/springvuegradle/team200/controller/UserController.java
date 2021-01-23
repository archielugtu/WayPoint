package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.dto.request.*;
import com.springvuegradle.team200.dto.response.*;
import com.springvuegradle.team200.exception.UserNotFoundException;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.Photo;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserEmailRepository;
import com.springvuegradle.team200.repository.UserRepository;
import com.springvuegradle.team200.service.UserEmailService;
import com.springvuegradle.team200.service.UserImageService;
import com.springvuegradle.team200.service.UserService;
import com.springvuegradle.team200.validator.ValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserEmailRepository userEmailRepository;

    @Autowired
    UserEmailService userEmailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserImageService userImageService;


    /**
     * Handles POST /profiles, registers user to the database
     * <p>
     * Registered user ID is returned if successful
     *
     * @param registerRequest Register User DTO
     * @param errors          errors found from validating DTO
     * @return User ID
     */
    @PostMapping("/profiles")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest registerRequest, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        User user = userService.create(registerRequest);
        return ResponseEntity.ok()
                .body(new RegisterResponse("Registered", user.getId().toString()));
    }

    /**
     * Handles GET /profiles/{userId}, retrieves a user's information from the database
     *
     * @param id User's ID
     * @return User info associated with the user's ID provided
     * @throws UserNotFoundException   when user with specified ID cannot be retrieved
     * @throws ResponseStatusException when attempting to retrieve the global admin's user profile
     */
    @GetMapping("/profiles/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable("userId") Long id) {
        User user = userService.read(id);
        return ResponseEntity
                .ok()
                .eTag(user.getVersion().toString())
                .body(new UserResponse(user));
    }

    /**
     * Handles PUT request to /profiles/{userId}, updates a user's data
     *
     * @param request            WebRequest containing request headers
     * @param editProfileRequest Request received containing user data
     * @param userId             User's ID
     * @param errors             Errors found in the request
     * @return Status code 200 if success
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}")
    public ResponseEntity<?> editProfile(@RequestBody @Valid EditProfileRequest editProfileRequest,
                                         Errors errors,
                                         WebRequest request,
                                         @PathVariable Long userId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        String ifMatchHeader = request.getHeader("If-Match");
        if (ifMatchHeader == null || ifMatchHeader.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.read(userId);

        if (!ifMatchHeader.equals(user.getVersion().toString())) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }

        userService.update(userId, editProfileRequest);

        return ResponseEntity
                .ok()
                .eTag(user.getVersion().toString())
                .body("User updated");
    }

    /**
     * Handles PUT request to /profiles/{userId}/emails, updates a user's emails
     *
     * @param userId           Id of the user
     * @param editEmailRequest Request received containing user data
     * @return status code 200 if ok, 404 if user not found, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}/emails")
    @ResponseBody
    public ResponseEntity<Void> editEmail(@PathVariable("userId") Long userId,
                                          @RequestBody EditEmailRequest editEmailRequest) {
        userService.update(userId, editEmailRequest);

        return ResponseEntity.ok().build();
    }

    /**
     * Handles PUT request to /profiles/{userId}/password, updates a user's password
     *
     * @param passwordRequest Request received containing user password
     * @param userId          Id of the user
     * @return status code 200 if ok, 400 if password not valid, 404 if user not found, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PostMapping("/profiles/{userId}/password")
    @ResponseBody()
    public ResponseEntity<?> changePassword(@RequestBody() @Valid PasswordRequest passwordRequest,
                                            Errors errors,
                                            @PathVariable Long userId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        boolean isRequestFromAdmin = jwtTokenUtil.getRequestingUser().getIsAdmin();
        userService.update(userId, passwordRequest, isRequestFromAdmin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profiles")
    @ResponseBody
    public ResponseEntity<?> getUser(@Valid SearchUserRequest searchUserRequest,
                                     Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }
        int size = 8;  // Restrict to 8 users per page of results

        User userSearching = jwtTokenUtil.getRequestingUser();
        Page<SearchUserResponse> resultPage = userService.readByQuery(size, searchUserRequest, userSearching);
        SearchUserPagedResponse searchUserPagedResponse = new SearchUserPagedResponse(
                resultPage.getContent(), resultPage.getTotalPages()
        );

        return ResponseEntity.status(HttpStatus.OK).body(searchUserPagedResponse);
    }

    @GetMapping("/profiles/search_email")
    public ResponseEntity<?> getUserIDByEmail(@Valid UserAdminStatusRequest userAdminStatusRequest,
                                              Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }
        List<String> emails = List.of(userAdminStatusRequest.getEmails().split(";"));
        List<UserAdminStatusResponse> responses = userService.readAdminStatusByEmail(emails);

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PreAuthorize("principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}/role")
    @ResponseBody
    public ResponseEntity<?> changeRole(@RequestBody @Valid RoleRequest roleRequest,
                                        Errors errors,
                                        @PathVariable("userId") Long userId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }
        userService.update(userId, roleRequest);
        return ResponseEntity.status(HttpStatus.OK).body("User's role has been updated");
    }

    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @DeleteMapping("/profiles/{userId}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/profiles/{userId}/role")
    @ResponseBody
    public ResponseEntity<Boolean> getUserIsAdmin(@PathVariable("userId") Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return ResponseEntity.status(HttpStatus.OK).body(user.getIsAdmin());
    }

    @GetMapping("/profiles/emails")
    public ResponseEntity<List<String>> getAllEmails() {
        List<String> userEmailList = userEmailService.read();
        return ResponseEntity.status(HttpStatus.OK).body(userEmailList);
    }

    /**
     * Handles a PUT request which sets a primary photo for a specific user
     *
     * @param userId The ID of the activity which the user wants to get the set primary photo
     * @return status code 200 if ok, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}/photos/primary")
    @ResponseBody
    public ResponseEntity<Long> uploadUserProfilePhoto(@PathVariable("userId") Long userId,
                                                    @RequestHeader("Content-Type") String contentType,
                                                    @RequestBody byte[] image) {
        PhotoRequest request = new PhotoRequest();
        request.setUserId(userId);
        request.setImage(image);
        request.setMediaType(MediaType.parseMediaType(contentType));
        request.setPrimary(true);
        Photo photo = userImageService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(photo.getId());
    }

    /**
     * Handles a PUT request which sets a cover photo for a specific user
     *
     * @param userId The ID of the activity which the user wants to get the set primary photo
     * @return status code 200 if ok, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}/photos/cover")
    @ResponseBody
    public ResponseEntity<Long> uploadUserCoverPhoto(@PathVariable("userId") Long userId,
                                                    @RequestHeader("Content-Type") String contentType,
                                                    @RequestBody byte[] image) {
        PhotoRequest request = new PhotoRequest();
        request.setUserId(userId);
        request.setImage(image);
        request.setMediaType(MediaType.parseMediaType(contentType));
        request.setPrimary(false);
        Photo photo = userImageService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(photo.getId());
    }


    /**
     * Handles a GET request to retrieve an user's hero (primary) image
     *
     * @param userId ID of the user
     * @return status code 200 if ok, 404 if image does not exist, 500 otherwise
     */
    @GetMapping("/profiles/{userId}/photos/primary")
    @ResponseBody
    public ResponseEntity<byte[]> retrieveUserHeroImg(@PathVariable("userId") Long userId) throws IOException {
        byte[] heroImage = userImageService.readHeroImage(userId);
        return ResponseEntity.status(HttpStatus.OK).body(heroImage);
    }

    /**
     * Handles a GET request to retrieve an user's cover image
     *
     * @param userId ID of the user
     * @return status code 200 if ok, 404 if image does not exist, 500 otherwise
     */
    @GetMapping("/profiles/{userId}/photos/cover")
    @ResponseBody
    public ResponseEntity<byte[]> retrieveUserCoverImg(@PathVariable("userId") Long userId) throws IOException {
        byte[] heroImage = userImageService.readCoverImage(userId);
        return ResponseEntity.status(HttpStatus.OK).body(heroImage);
    }

    /**
     * Handles DELETE photo request, deletes a specific user's photo
     *
     * @param userId The ID of the user which the user wants to delete the photo for
     * @return status code 200 if ok, 400 if not valid, 404 if user not found, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @DeleteMapping("/profiles/{userId}/photos/primary")
    public ResponseEntity<Void> deleteUserPrimaryPhoto(@PathVariable("userId") Long userId) {
        userImageService.delete(userId, true);
        return ResponseEntity.ok().build();
    }

    /**
     * Handles DELETE photo request, deletes a specific user's photo
     *
     * @param userId The ID of the user which the user wants to delete the photo for
     * @return status code 200 if ok, 400 if not valid, 404 if user not found, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @DeleteMapping("/profiles/{userId}/photos/cover")
        public ResponseEntity<Void> deleteUserCoverPhoto(@PathVariable("userId") Long userId) {
        userImageService.delete(userId, false);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profiles/emailIsInUse")
    public ResponseEntity<?> getEmailIsInUse(@Valid EmailIsInUseRequest request,
                                                   Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }
        boolean emailIsInUse = userEmailService.getEmailIsInUse(request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(emailIsInUse);
    }
}
