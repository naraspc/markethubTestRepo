package org.hanghae.markethub.global.constant;

public enum SuccessMessage {
    JOIN_SUCCESS_MESSAGE("회원가입이 완료되었습니다."),
    DELETE_SUCCESS_MESSAGE("회원탈퇴가 완료되었습니다.");

    private final String successMessage;

    SuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public String getSuccessMessage() {
        return "[SUCCESS] " + successMessage;
    }
}
