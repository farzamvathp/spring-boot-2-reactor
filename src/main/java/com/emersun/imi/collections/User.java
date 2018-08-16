package com.emersun.imi.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Document
public class User {
    @Id
    private String id;
    private Boolean hasSubscribed = false;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private String otpId = "";
    private LocalDateTime subscribeAt;
    private LocalDateTime unsubscribeAt;
    private Integer renewalCount = 0;

    public User(Boolean hasSubscribed, String phoneNumber) {
        this.hasSubscribed = hasSubscribed;
        this.phoneNumber = phoneNumber;
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusHours(1);
        this.subscribeAt = LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusHours(1);
        this.unsubscribeAt = LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusHours(1);
    }

    public User(String mobile) {
        this.phoneNumber = mobile;
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
    }

    public LocalDateTime getUnsubscribeAt() {
        return unsubscribeAt;
    }

    public void setUnsubscribeAt(LocalDateTime unsubscribeAt) {
        this.unsubscribeAt = unsubscribeAt;
    }

    public User() {
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getSubscribeAt() {
        return subscribeAt;
    }

    public void setSubscribeAt(LocalDateTime subscribeAt) {
        this.subscribeAt = subscribeAt;
    }

    public String getOtpId() {
        return otpId;
    }

    public void setOtpId(String otpId) {
        this.otpId = otpId;
    }


    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getHasSubscribed() {
        return hasSubscribed;
    }

    public void setHasSubscribed(Boolean hasSubscribed) {
        this.hasSubscribed = hasSubscribed;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
