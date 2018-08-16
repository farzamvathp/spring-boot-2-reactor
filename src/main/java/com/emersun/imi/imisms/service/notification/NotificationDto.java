package com.emersun.imi.imisms.service.notification;

public class NotificationDto {
    private String text;
    private String keyword;
    private String channel;
    private String notificationId;
    private String userid;
    private String messageTo;
    private String messageFrom;

    public NotificationDto(String text, String keyword, String channel, String notificationId, String userid, String messageTo, String messageFrom) {
        this.text = text;
        this.keyword = keyword;
        this.channel = channel;
        this.notificationId = notificationId;
        this.userid = userid;
        this.messageTo = messageTo;
        this.messageFrom = messageFrom;
    }

    public NotificationDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }
}
