package com.emersun.imi.panel.dto;

import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.utils.DateDeserializer;
import com.emersun.imi.utils.validators.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.vavr.control.Option;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAccountDto {
    private String id;
    @NotEmpty(message = "{validation.firstname.notempty}")
    private String firstname;
    @NotEmpty(message = "{validation.lastname.notempty}")
    private String lastname;
    @NotEmpty(message = "{validation.mobile.notempty}")
    private String mobile;
    @NotEmpty(message = "{validation.username.notempty}")
    private String username;
    @NotEmpty(message = "{validation.password.notempty}")
    private String password;
    private Boolean isBanned;
    @Role
    private String role;
    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDateTime canReadFromDate;
    private Set<Permission> permissions = new HashSet<>();

    public UserAccountDto() {
    }

    public UserAccountDto(UserAccount userAccount) {
        this.id = userAccount.getId();
        this.firstname = userAccount.getFirstname();
        this.lastname = userAccount.getLastname();
        this.mobile = userAccount.getMobile();
        this.username = userAccount.getUsername();
        this.isBanned = userAccount.getBanned();
        this.role = userAccount.getRole();
        Option.of(userAccount.getCanReadFromDate())
                .peek(localDateTime -> this.canReadFromDate = localDateTime);
        this.permissions = userAccount.getPermissions();
    }

    public UserAccountDto(@NotEmpty(message = "{validation.firstname.notempty}") String firstname, @NotEmpty(message = "{validation.lastname.notempty}") String lastname, @NotEmpty(message = "{validation.mobile.notempty}") String mobile, @NotEmpty(message = "{validation.username.notempty}") String username, @NotEmpty(message = "{validation.password.notempty}") String password, Boolean isBanned, String role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.mobile = mobile;
        this.username = username;
        this.password = password;
        this.isBanned = isBanned;
        this.role = role;
    }

    public UserAccountDto(String id, String username, String password, Boolean isBanned, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isBanned = isBanned;
        this.role = role;
    }

    public LocalDateTime getCanReadFromDate() {
        return canReadFromDate;
    }

    public void setCanReadFromDate(LocalDateTime canReadFromDate) {
        this.canReadFromDate = canReadFromDate;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
