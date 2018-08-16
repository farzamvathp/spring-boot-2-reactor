package com.emersun.imi.panel.service;

import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.panel.dto.DailyReport;
import com.emersun.imi.panel.dto.OveralReportDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportService {
    Mono<OveralReportDto> getVasOveralReport(String currentUsername);

    Flux<DailyReport> monthlySubscriptionInfo(Integer page, String queryType);

    Flux<DailyReport> getMonthlyReportsBasedOnPermission(Integer page, String queryType, String username);

    Flux<DailyReport> monthlyUserAccountInfo(Integer page, UserAccount manager);

    Flux<DailyReport> monthlyUserInfo(Integer page, UserAccount userAccount);
}
