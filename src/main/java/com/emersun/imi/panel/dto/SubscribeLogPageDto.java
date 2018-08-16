package com.emersun.imi.panel.dto;

import java.util.List;
import java.util.Set;

public class SubscribeLogPageDto extends AbstractPageDto {
    private List<SubscribeDto> content;

    public List<SubscribeDto> getContent() {
        return content;
    }

    public void setContent(List<SubscribeDto> content) {
        this.content = content;
    }
}
