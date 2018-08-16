package com.emersun.imi.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Document
public class SubscribeLog {
    @Id
    private String id;
    private String channel;
    private String userId;
    private String notificationId;
    private LocalDateTime createdAt;
    private String type;
    private String mobile;

    public SubscribeLog(String channel,String type, String userId, String notificationId,String mobile) {
        this.channel = channel;
        this.type = type;
        this.userId = userId;
        this.notificationId = notificationId;
        this.mobile = mobile;
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
    }

    public SubscribeLog(String channel, String userId, String notificationId, LocalDateTime createdAt, String type) {
        this.channel = channel;
        this.userId = userId;
        this.notificationId = notificationId;
        this.createdAt = createdAt;
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public SubscribeLog() {
        createdAt = LocalDateTime.now(ZoneId.of("Asia/Tehran"));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
