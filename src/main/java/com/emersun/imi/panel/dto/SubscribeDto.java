package com.emersun.imi.panel.dto;

import com.emersun.imi.collections.SubscribeLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubscribeDto {
    private String id;
    private String channel;
    private String notificationId;
    private LocalDateTime createdAt;
    private String type;
    private String mobile;

    public SubscribeDto(SubscribeLog subscribeLog) {
        this.id = subscribeLog.getId();
        this.channel = subscribeLog.getChannel();
        this.notificationId = subscribeLog.getNotificationId();
        this.createdAt = subscribeLog.getCreatedAt();
        this.type = subscribeLog.getType();
        this.mobile = subscribeLog.getMobile();
    }

    public SubscribeDto() {
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
