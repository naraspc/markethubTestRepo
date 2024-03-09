package org.hanghae.markethub.global.constant;

public enum ErrorMessage {
    TOKEN_NOT_EXIST_ERROR_MESSAGE("Not exist token"),
    INVALID_JWT_ERROR_MESSAGE("Invalid JWT signature"),
    EXPIRED_JWT_ERROR_MESSAGE("Expired JWT token"),
    UNSUPPORTED_JWT_ERROR_MESSAGE("Unsupported JWT token"),
    EMPTY_JWT_ERROR_MESSAGE("JWT claims is empty"),
    EMAIL_ALREADY_EXIST_ERROR_MESSAGE("Email already exists"),
    USER_NOT_FOUND_ERROR_MESSAGE("User not found"),
    PASSWORD_NOT_MATCH_ERROR_MESSAGE("Password not match"),;


    private final String errorMessage;

    ErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return "[ERROR] " + errorMessage;
    }
}
