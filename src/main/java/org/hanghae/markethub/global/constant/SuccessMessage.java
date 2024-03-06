package org.hanghae.markethub.global.constant;

public enum SuccessMessage {
    JOIN_SUCCESS_MESSAGE("Sign up success"),
    DELETE_SUCCESS_MESSAGE("Delete success"),
    LOGIN_SUCCESS_MESSAGE("Login success"),
    EMAIL_AVAILABLE_MESSAGE("Email available"),;



    private final String successMessage;

    SuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getSuccessMessage() {
        return "[SUCCESS] " + successMessage;
    }
}
