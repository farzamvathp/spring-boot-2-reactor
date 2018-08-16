package com.emersun.imi.imisms.service.notification;

import com.emersun.imi.collections.*;
import com.emersun.imi.imisms.service.IMIService;
import com.emersun.imi.repositories.ReportRepository;
import com.emersun.imi.repositories.SubscribeLogRepository;
import com.emersun.imi.repositories.UserRepository;
import com.emersun.imi.utils.Messages;
import com.emersun.imi.utils.messages.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;

@Service
public class CommandServiceImpl implements CommandService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscribeLogRepository subscribeLogRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private IMIService imiService;
    @Autowired
    private Messages messages;
    @Value("${vas.welcome.message}")
    private String welcomeMessage;
    @Value("${vas.service.name}")
    private String vasServiceName;
    @Override
    public Mono<ResponseEntity<Object>> subscription(NotificationDto notificationDto) {
        return userRepository.findByPhoneNumber(notificationDto.getMessageFrom())
                .switchIfEmpty(Mono.just(new User(notificationDto.getMessageFrom())))
                .doOnSuccess(user -> {
                    user.setHasSubscribed(true);
                    user.setSubscribeAt(LocalDateTime.now(ZoneId.of("Asia/Tehran")));
                })
                .flatMap(userRepository::save)
                .flatMap(user -> subscribeLogRepository.save(new SubscribeLog(notificationDto.getChannel(), SubscribeType.SUBSCRIBE.getName(), user.getId(), notificationDto.getNotificationId(),user.getPhoneNumber())))
                .log("new subscribe log saved waiting for imi request", Level.INFO, SignalType.ON_COMPLETE)
                .flatMap(subscribeLog -> imiService.sendIMIRequest(notificationDto.getMessageFrom(), welcomeMessage))
                .log("imi request already sent successfuly",Level.INFO, SignalType.ON_COMPLETE)
                .log("imi subscription request failed ", Level.INFO, SignalType.ON_ERROR)
                .flatMap(response -> reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()))
                .doOnSuccess(Report::increaseCountByOne)
                .flatMap(reportRepository::save)
                .flatMap(report -> reportRepository.findByType(ReportType.VAS_SMS_SUBSCRIBE.name()))
                .doOnSuccess(Report::increaseCountByOne)
                .flatMap(reportRepository::save)
                .log("VAS_SUBSCRIBE count increased by one", Level.INFO, SignalType.ON_COMPLETE)
                .flatMap(report -> Mono.just(ResponseEntity.ok().build()))
                .doOnError(throwable -> logger.error("Error in subscription method : ",throwable))
                .onErrorReturn(ResponseEntity.status(400).build());

    }

    @Override
    public Mono<ResponseEntity<Object>> unsubscription(NotificationDto notificationDto) {
        return userRepository.findByPhoneNumber(notificationDto.getMessageFrom())
                .switchIfEmpty(Mono.just(new User(notificationDto.getMessageFrom())))
                .doOnSuccess(user -> {
                    user.setHasSubscribed(false);
                    user.setUnsubscribeAt(LocalDateTime.now(ZoneId.of("Asia/Tehran")));
                })
                .flatMap(userRepository::save)
                .flatMap(user -> subscribeLogRepository.save(new SubscribeLog(notificationDto.getChannel(),SubscribeType.UNSUBSCRIBE.getName(),user.getId(),notificationDto.getNotificationId(),user.getPhoneNumber())))
                .log("new unsubscribe log saved waiting for imi request", Level.INFO, SignalType.ON_COMPLETE)
                .flatMap(subscribeLog -> imiService.sendIMIRequest(notificationDto.getMessageFrom(),String.format(messages.get(Response.MESSAGE_UNSUBSCRIBE),vasServiceName)))
                .log("imi request already sent successfuly", Level.INFO, SignalType.ON_COMPLETE)
                .log("imi unsubscription request failed ", Level.INFO, SignalType.ON_ERROR)
                .flatMap(response -> reportRepository.findByType(ReportType.VAS_UNSUBSCRIBE.name()))
                .doOnSuccess(Report::increaseCountByOne)
                .flatMap(reportRepository::save)
                .log("VAS_UNSUBSCRIBE count increased by one", Level.INFO, SignalType.ON_COMPLETE)
                .flatMap(report -> Mono.just(ResponseEntity.ok().build()))
                .doOnError(throwable -> logger.error("Error in unsubscription method : ",throwable))
                .onErrorReturn(ResponseEntity.status(400).build());
    }

    @Override
    public Mono<ResponseEntity<Object>> renewal(NotificationDto notificationDto) {
        return userRepository.findByPhoneNumber(notificationDto.getMessageFrom())
                .switchIfEmpty(Mono.just(new User(notificationDto.getMessageFrom())))
                .doOnSuccess(user -> user.setRenewalCount(user.getRenewalCount() + 1))
                .flatMap(userRepository::save)
                .flatMap(user -> subscribeLogRepository.save(new SubscribeLog(notificationDto.getChannel(), SubscribeType.RENEWAL.getName(),user.getId(),notificationDto.getNotificationId(),user.getPhoneNumber())))
                .flatMap(subscribeLog -> reportRepository.findByType(ReportType.VAS_SUCCESS_CHARGE.name()))
                .doOnSuccess(Report::increaseCountByOne)
                .flatMap(reportRepository::save)
                .flatMap(report -> Mono.just(ResponseEntity.ok().build()))
                .onErrorReturn(ResponseEntity.status(400).build());

    }

    @Override
    public Mono<ResponseEntity<Object>> failedRenewal(NotificationDto notificationDto) {
        return userRepository.findByPhoneNumber(notificationDto.getMessageFrom())
                .switchIfEmpty(Mono.just(new User(notificationDto.getMessageFrom())))
                .flatMap(userRepository::save)
                .flatMap(user -> subscribeLogRepository.save(new SubscribeLog(notificationDto.getChannel(),SubscribeType.FAILED_RENEWAL.getName(),user.getId(),notificationDto.getNotificationId(),user.getPhoneNumber())))
                .flatMap(subscribeLog -> reportRepository.findByType(ReportType.VAS_FAILED_CHARGE.name()))
                .doOnSuccess(Report::increaseCountByOne)
                .flatMap(reportRepository::save)
                .flatMap(subscribe -> Mono.just(ResponseEntity.ok().build()))
                .onErrorReturn(ResponseEntity.status(400).build());
    }

    @Override
    public Mono<ResponseEntity<Object>> subscribeOtpMessage(String otpId, String phone) {
        return userRepository.findByPhoneNumber(phone)
                .switchIfEmpty(Mono.just(new User(phone)))
                .doOnSuccess(user -> {
                    user.setHasSubscribed(true);
                    user.setSubscribeAt(LocalDateTime.now(ZoneId.of("Asia/Tehran")));
                })
                .flatMap(userRepository::save)
                .flatMap(user -> subscribeLogRepository.save(new SubscribeLog("OTP",SubscribeType.SUBSCRIBE.getName(),user.getId(),otpId,user.getPhoneNumber())))
                .log("new subscribe OTP log saved waiting for imi request", Level.INFO, SignalType.ON_COMPLETE)
                .flatMap(subscribeLog -> imiService.sendIMIRequest(phone,welcomeMessage))
                .log("imi request already sent successfuly", Level.INFO, SignalType.ON_COMPLETE)
                .log("imi subscription otp request failed ", Level.INFO, SignalType.ON_ERROR)
                .flatMap(response -> reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()))
                .doOnSuccess(Report::increaseCountByOne)
                .flatMap(reportRepository::save)
                .flatMap(report -> reportRepository.findByType(ReportType.VAS_OTP_SUBSCRIBE.name()))
                .doOnSuccess(Report::increaseCountByOne)
                .flatMap(reportRepository::save)
                .log("VAS_OTP_SUBSCRIBE increased by one", Level.INFO, SignalType.ON_COMPLETE)
                .flatMap(report -> Mono.just(ResponseEntity.ok().build()))
                .doOnError(throwable -> logger.error("Error in unsubscription method : ",throwable))
                .onErrorReturn(ResponseEntity.status(400).build());
    }
}
