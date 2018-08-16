package com.emersun.imi.panel.dto;

import java.time.LocalDateTime;

public class DailyReport {
    Long count;
    LocalDateTime dateTime;

    public DailyReport() {
    }

    public DailyReport(Long count, LocalDateTime dateTime) {
        this.count = count;
        this.dateTime = dateTime;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
