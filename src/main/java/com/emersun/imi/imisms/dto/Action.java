package com.emersun.imi.imisms.dto;

public enum Action {
    SMSSEND("smssend"),
    PUSHOTP("PushOtp"),
    CHARGEOTP("chargeotp");

    private String value;
    Action(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
