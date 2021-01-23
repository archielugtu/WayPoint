package com.springvuegradle.team200.config;

import com.springvuegradle.team200.exception.*;
import com.springvuegradle.team200.validator.ValidationErrorBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Defines exception handling for the whole application
 * <p>
 * If the exception is already defined in {@link ResponseEntityExceptionHandler}
 * we need to Override it to handle it manually
 * <p>
 * Otherwise, define our own exception handler with @ExceptionHandler annotation
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles HttpMessageNotReadable exception by returning BAD_REQUEST
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String error = ex.getLocalizedMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ValidationErrorBuilder.fromBindingErrors(error));
    }

    /**
     * Handles various xyzNotFound exception by returning NOT_FOUND
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            OutcomeQuestionNotFoundException.class,
            HeroImageNotFoundException.class,
            CoverImageNotFoundException.class
    })
    protected ResponseEntity<Object> handleUserNotFound(RuntimeException ex) {
        String error = ex.getLocalizedMessage();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ValidationErrorBuilder.fromBindingErrors(error));
    }

    /**
     * Handles various InvalidXYZ exception by returning BAD_REQUEST
     */
    @ExceptionHandler({
            InvalidActivityTypeException.class,
            OutcomeMultichoiceOptionNotFoundException.class,
            ActivityNotFoundException.class,
            EmailNotFoundException.class,
            EmailAlreadyRegisteredException.class,
            EmailRegisteredWithAnotherUserException.class,
            InvalidPasswordException.class,
            MaxEmailCountReachedException.class,
            PassportCountryNotFoundException.class,
            InvalidAnswerException.class,
            InvalidOutcomeQuestionException.class,
            NumberFormatException.class,
            UnableToDeleteImageException.class,
            UnableToSaveImageException.class,
            UnsupportedImageTypeException.class,
            ImageNotFoundException.class,
            InvalidImageSizeException.class,
            InvalidNumberOfPrimaryPhotosException.class,
            PhotosLimitExceededException.class,
            NullIdException.class
    })
    protected ResponseEntity<Object> handleInvalidRequestException(RuntimeException ex) {
        String error = ex.getLocalizedMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ValidationErrorBuilder.fromBindingErrors(error));
    }


    /**
     * Handles InsufficientPermissionException by returning UNAUTHORIZED
     */
    @ExceptionHandler({
            InsufficientPermissionException.class,
    })
    protected ResponseEntity<Object> handleInsufficientPermission(RuntimeException ex) {
        String error = ex.getLocalizedMessage();
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ValidationErrorBuilder.fromBindingErrors(error));
    }

    /**
     * Handles various exceptions that return FORBIDDEN status code
     */
    @ExceptionHandler({
            ChangeCreatorRoleNotPermittedException.class,
    })
    protected ResponseEntity<Object> handleForbidden(RuntimeException ex) {
        String error = ex.getLocalizedMessage();
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ValidationErrorBuilder.fromBindingErrors(error));
    }
}
