package com.emersun.imi.panel.dto;

import javax.validation.constraints.NotEmpty;

public class LoginDto {
    @NotEmpty(message = "{validation.username.notempty}")
    private String username;
    @NotEmpty(message = "{validation.password.notempty}")
    private String password;

    public LoginDto() {
    }

    public LoginDto(@NotEmpty(message = "{validation.username.notempty}") String username, @NotEmpty(message = "{validation.password.notempty}") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
