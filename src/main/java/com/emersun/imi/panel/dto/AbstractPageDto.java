package com.emersun.imi.panel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractPageDto {
    private Boolean hasNext;
    private Integer currentPage;
    private Long totalNumberOfPages;

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotalNumberOfPages() {
        return totalNumberOfPages;
    }

    public void setTotalNumberOfPages(Long totalNumberOfPages) {
        this.totalNumberOfPages = totalNumberOfPages;
    }
}
