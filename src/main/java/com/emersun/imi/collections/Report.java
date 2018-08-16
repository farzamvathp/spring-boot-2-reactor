package com.emersun.imi.collections;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Report {
    private String id;
    private Long count;
    private String type;

    public void increaseCountByOne() {
        this.count += 1;
    }

    public Report(Long count, String type) {
        this.count = count;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
