package org.thivernale.booknetwork.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "New password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "Account locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "Account disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Invalid login credentials");

    private final int code;
    private final HttpStatus httpStatus;
    private final String description;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
