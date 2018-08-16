package com.emersun.imi.panel.dto;

import javax.validation.constraints.NotEmpty;

public class ChangePasswordDto {
    @NotEmpty(message = "{validation.oldpass.notempty}")
    private String currentPassword;
    @NotEmpty(message = "{validation.newpass.notempty}")
    private String newPassword;
    @NotEmpty(message = "{validation.newpass.notempty}")
    private String retryNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRetryNewPassword() {
        return retryNewPassword;
    }

    public void setRetryNewPassword(String retryNewPassword) {
        this.retryNewPassword = retryNewPassword;
    }
}
