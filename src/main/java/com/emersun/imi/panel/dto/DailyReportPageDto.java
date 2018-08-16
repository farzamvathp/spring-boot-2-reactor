package com.emersun.imi.panel.dto;

import java.util.List;
import java.util.Set;

public class DailyReportPageDto extends AbstractPageDto {
    private List<DailyReport> content;

    public List<DailyReport> getContent() {
        return content;
    }

    public void setContent(List<DailyReport> content) {
        this.content = content;
    }
}
