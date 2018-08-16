package com.emersun.imi.utils.messages;

public enum Response {
    INTERNAL_SERVER_ERROR("error.internal.server.error"),
    SUCCESS("operation.successful"),
    MESSAGE_ERROR("vas.message.error"),
    MESSAGE_UNSUBSCRIBE("vas.message.unsubscribe"),
    USERNAME_NOT_FOUND("error.username.not.found"),
    ACCESS_DENIED("error.access.denied"),
    ACCOUNT_ALREADY_EXISTS("error.account.already.exists"),
    OLD_NEW_PASS_EQUALS("error.old.new.password.equal"),
    NEW_RETRY_PASS_NOTEQUAL("error.new.retry.pass.notequal");

    Response(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return message;
    }
}
