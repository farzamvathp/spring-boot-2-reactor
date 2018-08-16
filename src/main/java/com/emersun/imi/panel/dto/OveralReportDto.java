package com.emersun.imi.panel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OveralReportDto {
    private Long registeredCount;
    private Long successChargeCount;
    private Long vasSubCount;
    private Long vasUnsubCount;
    private Long vasSMSSubCount;
    private Long vasOTPSubCount;
    private Long managerCount;
    private Long countOfCurrentSubscribedUsers;
    private Long countOfCurrentUnsubscribedUsers;
    private Long failedChargeCount;

    public Long getFailedChargeCount() {
        return failedChargeCount;
    }

    public void setFailedChargeCount(Long failedChargeCount) {
        this.failedChargeCount = failedChargeCount;
    }

    public Long getCountOfCurrentSubscribedUsers() {
        return countOfCurrentSubscribedUsers;
    }

    public void setCountOfCurrentSubscribedUsers(Long countOfCurrentSubscribedUsers) {
        this.countOfCurrentSubscribedUsers = countOfCurrentSubscribedUsers;
    }

    public Long getCountOfCurrentUnsubscribedUsers() {
        return countOfCurrentUnsubscribedUsers;
    }

    public void setCountOfCurrentUnsubscribedUsers(Long countOfCurrentUnsubscribedUsers) {
        this.countOfCurrentUnsubscribedUsers = countOfCurrentUnsubscribedUsers;
    }

    public Long getVasSubCount() {
        return vasSubCount;
    }

    public void setVasSubCount(Long vasSubCount) {
        this.vasSubCount = vasSubCount;
    }

    public Long getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(Long registeredCount) {
        this.registeredCount = registeredCount;
    }

    public Long getSuccessChargeCount() {
        return successChargeCount;
    }

    public void setSuccessChargeCount(Long successChargeCount) {
        this.successChargeCount = successChargeCount;
    }

    public Long getVasUnsubCount() {
        return vasUnsubCount;
    }

    public void setVasUnsubCount(Long vasUnsubCount) {
        this.vasUnsubCount = vasUnsubCount;
    }

    public Long getVasSMSSubCount() {
        return vasSMSSubCount;
    }

    public void setVasSMSSubCount(Long vasSMSSubCount) {
        this.vasSMSSubCount = vasSMSSubCount;
    }

    public Long getVasOTPSubCount() {
        return vasOTPSubCount;
    }

    public void setVasOTPSubCount(Long vasOTPSubCount) {
        this.vasOTPSubCount = vasOTPSubCount;
    }

    public Long getManagerCount() {
        return managerCount;
    }

    public void setManagerCount(Long managerCount) {
        this.managerCount = managerCount;
    }
}
