package app.test.techtask.controllers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler handles validation errors across the application.
 *
 * When a ConstraintViolationException occurs, it collects all error messages
 * from the validation failures and returns them as a plain text response with
 * a 400 Bad Request status.
 *
 * Method:
 * - handleConstraintViolationException(ConstraintViolationException ex):
 *   Collects and formats error messages, then returns them with a 400 status.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        StringBuilder messages = new StringBuilder();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            messages.append("ERROR Message='")
                    .append(violation.getMessage())
                    .append("'\n");
        }

        return new ResponseEntity<>(messages.toString().trim(), HttpStatus.BAD_REQUEST);
    }

}