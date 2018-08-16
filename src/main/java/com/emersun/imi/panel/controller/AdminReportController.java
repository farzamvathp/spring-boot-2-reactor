package com.emersun.imi.panel.controller;

import com.emersun.imi.imisms.service.SubscribeLogService;
import com.emersun.imi.panel.dto.*;
import com.emersun.imi.panel.service.ReportService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@CrossOrigin(origins = "*", allowedHeaders = "*",
//        methods = {RequestMethod.DELETE,RequestMethod.GET,
//                RequestMethod.OPTIONS,RequestMethod.POST,
//                RequestMethod.PUT})
@RestController
@RequestMapping("${api.base.url}" + "/admin/reports")
public class AdminReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private SubscribeLogService subscribeLogService;

    @ApiOperation(value = "overall reports",response = OveralReportDto.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @GetMapping("/overall")
    public Mono<OveralReportDto> overalReport() {
        return reportService.getVasOveralReport("salam");
    }


    @ApiOperation(value = "overall reports based on managers permissions", response = OveralReportDto.class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @GetMapping("/permission-based-overall")
    public Mono<OveralReportDto> permissionBasedOveralReports(Principal principal) {
        return reportService.overallReport(principal.getName());
    }


    @ApiOperation(value = "daily reports of subscriptions. subscribeType path variable must be one of these values : " +
            "sms_subscribe / unsubscribe / otp_subscribe / renewal / subscribe / failed_renewal",
    response = DailyReport[].class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @GetMapping("/subscriptions/{subscriptionType}")
    public Flux<DailyReport> dailyReport(@RequestParam(required = false) Optional<Integer> page, @PathVariable String subscriptionType) {
        return reportService.monthlySubscriptionInfo(page.orElse(0),subscriptionType);
    }

    @ApiOperation(value="daily reports queryType can be one of these values  MANAGERS,\n" +
            "    REGISTEREDS,\n" +
            "    VAS_SUB,\n" +
            "    VAS_UNSUB,\n" +
            "    RENEWALS,\n" +
            "    FAILED_RENEWALS,\n" +
            "    SMS_SUB,\n" +
            "    OTP_SUB,\n" +
            "    CURRENT_VAS_SUB,\n" +
            "    CURRENT_VAS_UNSUB" , response = DailyReport[].class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @GetMapping("/detailed/{queryType}")
    public Mono<DailyReportPageDto> getDetailedReports(@RequestParam(required = false) Optional<Integer> page, @PathVariable String queryType, Principal principal) {
        DailyReportPageDto detailedReport = new DailyReportPageDto();
        return reportService.getMonthlyReportsBasedOnPermission(page.orElse(0),queryType,principal.getName())
                .collect(Collectors.toSet())
                .doOnSuccess(set -> {
                    detailedReport.setContent(new ArrayList<>(set));
                    detailedReport.getContent().sort(Comparator.comparing(DailyReport::getDateTime).reversed());
                })
                .flatMap(set ->
                        reportService.getMonthlyReportsBasedOnPermission(page.orElse(0) + 1,queryType,principal.getName()).hasElements())
                .doOnSuccess(detailedReport::setHasNext)
                .map(b -> page.orElse(0))
                .doOnSuccess(detailedReport::setCurrentPage)
                .map(b -> detailedReport);
    }

    @ApiOperation(value = "detailed report of subscriptions", response = SubscribeDto[].class)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @GetMapping("/subscribe_log")
    public Mono<SubscribeLogPageDto> subscribe_log(@RequestParam(required = false) Optional<String> mobile,
                                            @RequestParam(required = false) Optional<Integer> page,
                                            @RequestParam(required = false) Optional<Integer> size) {
        SubscribeLogPageDto pageDto = new SubscribeLogPageDto();

        return subscribeLogService.getSubsribeLogs(PageRequest.of(page.orElse(0),size.orElse(10)),mobile)
                .collect(Collectors.toSet())
                .doOnSuccess(set -> {
                    pageDto.setContent(new ArrayList<>(set));
                    pageDto.getContent().sort(Comparator.comparing(SubscribeDto::getCreatedAt).reversed());
                })
                .flatMap(s -> subscribeLogService.totalNumberOfElements(PageRequest.of(page.orElse(0),size.orElse(10)),mobile))
                .doOnSuccess(count -> pageDto.setTotalNumberOfPages(count/size.orElse(10)))
                .map(s -> page.orElse(0))
                .doOnSuccess(pageDto::setCurrentPage)
                .map(s -> pageDto);
    }
}
