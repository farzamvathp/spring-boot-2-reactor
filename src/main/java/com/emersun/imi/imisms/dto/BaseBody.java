package com.emersun.imi.imisms.dto;

public class BaseBody {
    private Integer serviceid;
    private Recipient recipient;
    private String type;

    public BaseBody() {
    }

    public BaseBody(Integer serviceid, Recipient recipient, String type) {
        this.serviceid = serviceid;
        this.recipient = recipient;
        this.type = type;
    }

    public BaseBody(Integer serviceid, Recipient recipient) {
        this.serviceid = serviceid;
        this.recipient = recipient;
    }

    public Integer getServiceid() {
        return serviceid;
    }

    public void setServiceid(Integer serviceid) {
        this.serviceid = serviceid;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
