package org.thivernale.booknetwork.handler;

import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;
import static org.thivernale.booknetwork.handler.BusinessErrorCodes.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException lockedException) {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(
                ExceptionResponse.builder()
                    .businessErrorCode(ACCOUNT_LOCKED.getCode())
                    .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                    .error(lockedException.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException disabledException) {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(
                ExceptionResponse.builder()
                    .businessErrorCode(ACCOUNT_DISABLED.getCode())
                    .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                    .error(disabledException.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException badCredentialsException) {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(
                ExceptionResponse.builder()
                    .businessErrorCode(BAD_CREDENTIALS.getCode())
                    .businessErrorDescription(BAD_CREDENTIALS.getDescription())
                    .error(BAD_CREDENTIALS.getDescription())
                    .build()
            );
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException messagingException) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(
                ExceptionResponse.builder()
                    .error(messagingException.getMessage())
                    .build()
            );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException validException) {
        Set<String> validationErrors = new HashSet<>();
        validException.getBindingResult()
            .getAllErrors()
            .forEach(error -> validationErrors.add(error.getDefaultMessage()));

        return ResponseEntity.status(BAD_REQUEST)
            .body(
                ExceptionResponse.builder()
                    .validationErrors(validationErrors)
                    .build()
            );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        // TODO log the exception
        exception.printStackTrace();

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(
                ExceptionResponse.builder()
                    .businessErrorDescription("Internal server error, please contact admin")
                    .error(exception.getMessage())
                    .build()
            );
    }
}
