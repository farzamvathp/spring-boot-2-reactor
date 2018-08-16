package com.emersun.imi.panel.service;

import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.ReportType;
import com.emersun.imi.collections.SubscribeType;
import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.panel.dto.DailyReport;
import com.emersun.imi.panel.dto.OveralReportDto;
import com.emersun.imi.repositories.ReportRepository;
import com.emersun.imi.repositories.SubscribeLogRepository;
import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.repositories.UserRepository;
import com.emersun.imi.utils.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.vavr.API.*;
import static io.vavr.Predicates.is;

@Service
public class ReportServiceImpl implements ReportService {
    private final static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);
    @Autowired
    private SubscribeLogRepository subscribeLogRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private Messages messages;

    @Override
    public Mono<OveralReportDto> getVasOveralReport(String currentUsername) {
        OveralReportDto overalReportDto = new OveralReportDto();
        return reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name())
                .doOnSuccess(report -> overalReportDto.setVasSubCount(report.getCount()))
                .flatMap(report -> reportRepository.findByType(ReportType.VAS_SMS_SUBSCRIBE.name()))
                .doOnSuccess(report -> overalReportDto.setVasSMSSubCount(report.getCount()))
                .flatMap(report -> reportRepository.findByType(ReportType.VAS_OTP_SUBSCRIBE.name()))
                .doOnSuccess(report -> overalReportDto.setVasOTPSubCount(report.getCount()))
                .flatMap(report -> reportRepository.findByType(ReportType.VAS_SUCCESS_CHARGE.name()))
                .doOnSuccess(report -> overalReportDto.setSuccessChargeCount(report.getCount()))
                .flatMap(report -> reportRepository.findByType(ReportType.VAS_UNSUBSCRIBE.name()))
                .doOnSuccess(report -> overalReportDto.setVasUnsubCount(report.getCount()))
                .flatMap(report -> userRepository.countByHasSubscribed(true))
                .doOnSuccess(overalReportDto::setCountOfCurrentSubscribedUsers)
                .flatMap(count -> userRepository.countByHasSubscribed(false))
                .doOnSuccess(overalReportDto::setCountOfCurrentUnsubscribedUsers)
                .flatMap(r -> userRepository.count())
                .doOnSuccess(overalReportDto::setRegisteredCount)
                .flatMap(u -> userAccountRepository.count())
                .doOnSuccess(overalReportDto::setManagerCount)
                .map(report -> overalReportDto);
    }


    @Override
    public Flux<DailyReport> monthlySubscriptionInfo(Integer page, String queryType) {

        return Match(queryType.toLowerCase()).of(
                Case($("sms_subscribe"),
                        subscribeLogRepository.findByTypeAndChannel(SubscribeType.SUBSCRIBE.getName(),"SMS")),
                Case($("unsubcribe"),
                        subscribeLogRepository.findByType(SubscribeType.UNSUBSCRIBE.getName())),
                Case($("otp_subscribe"),
                        subscribeLogRepository.findByTypeAndChannel(SubscribeType.SUBSCRIBE.getName(),"OTP")),
                Case($("renewal"),
                        subscribeLogRepository.findByType(SubscribeType.RENEWAL.getName())),
                Case($("subscribe"),
                        subscribeLogRepository.findByType(SubscribeType.SUBSCRIBE.getName())),
                Case($("failed_renewal"),
                        subscribeLogRepository.findByType(SubscribeType.FAILED_RENEWAL.getName())))
                .filter(subscribeLog -> monthPaginationPredicate(subscribeLog.getCreatedAt(),page))
                .collect(Collectors.groupingBy(sub -> sub.getCreatedAt().getDayOfYear(),Collectors.counting()))
        .flux()
        .flatMap(this::transformDayOfYearEntrySetToDailyReport);
    }

    @Override
    public Flux<DailyReport> getMonthlyReportsBasedOnPermission(Integer page, String queryType, String username) {
        return userAccountService.checkCurrentUserPermissionsBasedOnQueryType(username,queryType)
                .flux()
                .flatMap(userAccount -> {
                            Permission permission = Enum.valueOf(Permission.class,queryType);
                            if(Arrays.asList(
                                    Permission.SMS_SUB,
                                    Permission.VAS_SUB,
                                    Permission.VAS_UNSUB,
                                    Permission.OTP_SUB,
                                    Permission.RENEWALS,
                                    Permission.FAILED_RENEWALS).contains(permission)) {
                                return getSubscriptionLogMonthlyInfo(page,queryType,userAccount);
                            } else if(Arrays.asList(Permission.CURRENT_VAS_SUB,Permission.CURRENT_VAS_UNSUB).contains(permission)) {
                                return monthlyUserSubscriptionInfoBasedOnPermission(page,queryType,userAccount);
                            } else if(permission.equals(Permission.MANAGERS)) {
                                return monthlyUserAccountInfo(page,userAccount);
                            } else if(permission.equals(Permission.REGISTEREDS)) {
                                return monthlyUserInfo(page,userAccount);
                            } else {
                                return Flux.empty();
                            }
                        })
                .doOnError(throwable -> logger.error("Error in getMonthlyReportsBasedOnPermission ",throwable));
    }

    private Flux<DailyReport> getSubscriptionLogMonthlyInfo(Integer page,String queryType,UserAccount userAccount) {

        return Match(Enum.valueOf(Permission.class,queryType)).of(
                Case($(is(Permission.SMS_SUB)),
                        subscribeLogRepository.findByTypeAndChannel(SubscribeType.SUBSCRIBE.getName(),"SMS")),
                Case($(is(Permission.VAS_UNSUB)),
                        subscribeLogRepository.findByType(SubscribeType.UNSUBSCRIBE.getName())),
                Case($(is(Permission.OTP_SUB)),
                        subscribeLogRepository.findByTypeAndChannel(SubscribeType.SUBSCRIBE.getName(),"OTP")),
                Case($(is(Permission.RENEWALS)),
                        subscribeLogRepository.findByType(SubscribeType.RENEWAL.getName())),
                Case($(is(Permission.VAS_SUB)),
                        subscribeLogRepository.findByType(SubscribeType.SUBSCRIBE.getName())),
                Case($(is(Permission.FAILED_RENEWALS)),
                        subscribeLogRepository.findByType(SubscribeType.FAILED_RENEWAL.getName())))
                .filter(subscribeLog -> subscribeLog.getCreatedAt().isAfter(userAccount.getCanReadFromDate()))
                .filter(subscribeLog -> monthPaginationPredicate(subscribeLog.getCreatedAt(),page))
                .collect(Collectors.groupingBy(sub -> sub.getCreatedAt().getDayOfYear(),Collectors.counting()))
                .flux()
                .flatMap(this::transformDayOfYearEntrySetToDailyReport)
                .doOnError(throwable -> logger.error("Error in getSubscriptionLogMonthlyInfo ",throwable));
    }

    public Flux<DailyReport> monthlyUserSubscriptionInfoBasedOnPermission(Integer page,String queryType,UserAccount userAccount) {
        Permission permission = Enum.valueOf(Permission.class,queryType);
        if(permission.equals(Permission.CURRENT_VAS_SUB)) {
            return userRepository.findByHasSubscribed(true)
                    .filter(u -> u.getCreatedAt().isAfter(userAccount.getCanReadFromDate()))
                    .filter(u -> monthPaginationPredicate(u.getSubscribeAt(),page))
                    .collect(Collectors.groupingBy(u -> u.getSubscribeAt().getDayOfYear(),Collectors.counting()))
                    .flux().flatMap(this::transformDayOfYearEntrySetToDailyReport);
        } else {
            return userRepository.findByHasSubscribed(false)
                    .filter(u -> u.getCreatedAt().isAfter(userAccount.getCanReadFromDate()))
                    .filter(u -> monthPaginationPredicate(u.getUnsubscribeAt(),page))
                    .collect(Collectors.groupingBy(u -> u.getUnsubscribeAt().getDayOfYear(),Collectors.counting()))
                    .flux().flatMap(this::transformDayOfYearEntrySetToDailyReport);
        }
    }

    @Override
    public Flux<DailyReport> monthlyUserAccountInfo(Integer page,UserAccount manager) {
        return userAccountRepository.findAll()
                .filter(userAccount -> userAccount.getCreatedAt().isAfter(manager.getCanReadFromDate()))
                .filter(userAccount -> monthPaginationPredicate(userAccount.getCreatedAt(),page))
                .collect(Collectors.groupingBy(acc -> acc.getCreatedAt().getDayOfYear(),Collectors.counting()))
                .flux()
                .flatMap(this::transformDayOfYearEntrySetToDailyReport);
    }

    @Override
    public Flux<DailyReport> monthlyUserInfo(Integer page,UserAccount userAccount) {
        return userRepository.findAll()
                .filter(user -> user.getCreatedAt().isAfter(userAccount.getCanReadFromDate()))
                .filter(user -> monthPaginationPredicate(user.getCreatedAt(),page))
                .collect(Collectors.groupingBy(user -> user.getCreatedAt().getDayOfYear(),Collectors.counting()))
                .flux()
                .flatMap(this::transformDayOfYearEntrySetToDailyReport);
    }

    public Flux<DailyReport> transformDayOfYearEntrySetToDailyReport(Map<Integer,Long> map) {
        int min = map.keySet().stream().mapToInt(Integer::intValue).min().orElse(0);
        int max = map.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        IntStream.range(min,max).forEach(i -> map.putIfAbsent(i,0L));
        return Flux.fromStream(map.entrySet().stream().map(entry -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_YEAR,entry.getKey());
            return new DailyReport(entry.getValue(),
                    LocalDateTime.ofInstant(calendar.toInstant(),ZoneId.of("Asia/Tehran")));
        })).sort(Comparator.comparing(DailyReport::getDateTime).reversed());
    }


    private boolean monthPaginationPredicate(LocalDateTime localDateTime,Integer page) {
        return page <= 0 ?
                localDateTime != null && localDateTime.isAfter(LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusMonths(1)) :
                localDateTime != null && localDateTime.isAfter(LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusMonths(page + 1)) &&
                        localDateTime.isBefore(LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusMonths(page));
    }
}