package com.emersun.imi.imisms.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.*;

@JacksonXmlRootElement(localName = "recipient")
public class Recipient {
    @JacksonXmlProperty(isAttribute = true,localName = "mobile")
    private String mobile;
    @JacksonXmlProperty(isAttribute = true,localName = "originator")
    private String originator;
    @JacksonXmlProperty(isAttribute = true,localName = "cost")
    private Integer cost = 0;
    @JacksonXmlText
    private Object value;
    @JacksonXmlProperty(isAttribute = true,localName = "doerId")
    private Long doerId;
    @JacksonXmlProperty(isAttribute = true,localName = "pin")
    private String pin;
    @JacksonXmlProperty(isAttribute = true,localName = "status")
    private Integer status;
    @JacksonXmlProperty(isAttribute = true,localName = "Ml")
    private Byte Ml;

    public Recipient() {
    }

    public Recipient(String mobile, String originator, Integer cost) {
        this.mobile = mobile;
        this.originator = originator;
        this.cost = cost;
    }

    public Recipient(String mobile, String originator, Integer cost, Object value) {
        this.mobile = mobile;
        this.originator = originator;
        this.cost = cost;
        this.value = value;
    }

    public Recipient(String mobile, String originator, Object value, String pin) {
        this.mobile = mobile;
        this.originator = originator;
        this.value = value;
        this.pin = pin;
    }

    public Recipient(String mobile, String originator, Integer cost, Object value, Long doerId, String pin, Integer status, Byte ml) {
        this.mobile = mobile;
        this.originator = originator;
        this.cost = cost;
        this.value = value;
        this.doerId = doerId;
        this.pin = pin;
        this.status = status;
        Ml = ml;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getDoerId() {
        return doerId;
    }

    public void setDoerId(Long doerId) {
        this.doerId = doerId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Byte getMl() {
        return Ml;
    }

    public void setMl(Byte ml) {
        Ml = ml;
    }
}
