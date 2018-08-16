package com.emersun.imi.security;

import com.emersun.imi.collections.Permission;

import java.util.HashSet;
import java.util.Set;

public class TokenModel {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireTime;
    private Long refreshTokenExpireTime;
    private String tokenType = "Bearer ";
    private String role;
    private Set<Permission> permissions = new HashSet<>();

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getAccessTokenExpireTime() {
        return accessTokenExpireTime;
    }

    public void setAccessTokenExpireTime(Long accessTokenExpireTime) {
        this.accessTokenExpireTime = accessTokenExpireTime;
    }

    public Long getRefreshTokenExpireTime() {
        return refreshTokenExpireTime;
    }

    public void setRefreshTokenExpireTime(Long refreshTokenExpireTime) {
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
