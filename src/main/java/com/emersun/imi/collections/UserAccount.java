package com.emersun.imi.collections;

import com.emersun.imi.configs.Constants;
import com.emersun.imi.panel.dto.UserAccountDto;
import com.google.common.collect.Sets;
import io.vavr.control.Option;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Document
public class UserAccount {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String mobile;
    private String username;
    private String password;
    private Boolean isBanned;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime canReadFromDate;
    private Set<Permission> permissions = new HashSet<>();

    public UserAccount edit(UserAccountDto userAccountDto) {
        this.firstname = userAccountDto.getFirstname();
        this.lastname = userAccountDto.getLastname();
        this.mobile = userAccountDto.getMobile();
        Option.of(userAccountDto.getCanReadFromDate())
                .peek(timestamp -> this.canReadFromDate = timestamp)
                .onEmpty(() -> this.canReadFromDate = this.createdAt);
        Option.of(userAccountDto.getPermissions())
                .filter(permissions -> !permissions.isEmpty())
                .orElse(Option.of(Sets.newHashSet(Permission.values())))
                .peek(permissions -> this.permissions = permissions);
        Option.of(userAccountDto.getRole())
                .filter(r -> !r.isEmpty())
                .orElse(Option.of(Constants.OPERATOR_ROLE))
                .peek(role -> this.setRole(role));
        return this;
    }

    public UserAccount() {
    }

    public UserAccount(UserAccountDto userAccountDto) {
        this.firstname = userAccountDto.getFirstname();
        this.lastname = userAccountDto.getLastname();
        this.mobile = userAccountDto.getMobile();
        this.username = userAccountDto.getUsername();
        this.password = userAccountDto.getPassword();
        this.isBanned = false;
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
        Option.of(userAccountDto.getCanReadFromDate())
                .peek(timestamp -> this.canReadFromDate = timestamp)
                .onEmpty(() -> this.canReadFromDate = this.createdAt);
        Option.of(userAccountDto.getPermissions())
                .filter(permissions -> !permissions.isEmpty())
                .orElse(Option.of(Sets.newHashSet(Permission.values())))
                .peek(permissions -> this.permissions = permissions);
        Option.of(userAccountDto.getRole())
                .filter(r -> !r.isEmpty())
                .orElse(Option.of(Constants.OPERATOR_ROLE))
                .peek(role -> this.setRole(role));
    }

    public UserAccount(String username, String password, Boolean isBanned, String role) {
        this.username = username;
        this.password = password;
        this.isBanned = isBanned;
        this.role = role;
        this.canReadFromDate = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
    }

    public UserAccount(String username, String password, Boolean isBanned, String role, Set<Permission> permissions) {
        this.username = username;
        this.password = password;
        this.isBanned = isBanned;
        this.role = role;
        this.permissions = permissions;
        this.canReadFromDate = LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusYears(1);
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusHours(1);
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public LocalDateTime getCanReadFromDate() {
        return canReadFromDate;
    }

    public void setCanReadFromDate(LocalDateTime canReadFromDate) {
        this.canReadFromDate = canReadFromDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
    }
}
