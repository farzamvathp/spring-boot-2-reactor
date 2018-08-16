package com.emersun.imi.imisms.service;

import com.emersun.imi.collections.User;
import com.emersun.imi.exceptions.AlreadySubscribedException;
import com.emersun.imi.exceptions.AlreadyUnsubscribedException;
import com.emersun.imi.exceptions.BadRequestException;
import com.emersun.imi.imisms.service.notification.CommandService;
import com.emersun.imi.imisms.service.notification.NotificationDto;
import com.emersun.imi.repositories.UserRepository;
import com.emersun.imi.utils.Messages;
import com.emersun.imi.utils.messages.Response;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.stream.IntStream;

import static io.vavr.API.*;

@Service
public class VasServiceImpl implements VasService {
    @Value("${vas.userid}")
    private String vasUserId;
    @Autowired
    private IMIService imiService;
    @Autowired
    private CommandService commandService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Messages messages;
    @Value("${vas.service.name}")
    private String vasServiceName;

    @Override
    public ResponseEntity<?> message(String text, String smsId, String to, String from, String userid) {
        if(!userid.equals(vasUserId))
            return ResponseEntity.status(401).build();
        if(from.length() < 11)
            from = "0" + from;
        if(text.equals("Subscription") ||
                text.equals("Unsubscription") ||
                text.equals("Renewal") ||
                text.equals("FailedRenewal") ||
                text.equals("BarredPostPaid"))
            return ResponseEntity.status(202).build();
        imiService.sendIMIRequest(from, String.format(messages.get(Response.MESSAGE_ERROR),vasServiceName)).subscribe();
        return ResponseEntity.status(200).build();
    }

    @Override
    public Mono<? extends ResponseEntity<?>> notification(String text, String keyword, String channel, String notificationId, String userid, String to, String from) {
        if(!userid.equals(vasUserId))
            return Mono.just(ResponseEntity.status(401).build());
        if(from.length() < 11)
            from = "0" + from;
        NotificationDto notificationDto =
                new NotificationDto(text,keyword,channel,notificationId,userid,to,from);
        return Match(text).of(
                Case($("Subscription"), () ->
                    Option.of(channel)
                            .filter(ch -> !ch.equals("OTP"))
                            .map(ch -> commandService.subscription(notificationDto))
                            .getOrElse(Mono.just(ResponseEntity.ok().build()))
                ),
                Case($("Unsubscription"), commandService.unsubscription(notificationDto)),
                Case($("Renewal"), commandService.renewal(notificationDto)),
                Case($("FailedRenewal"), commandService.failedRenewal(notificationDto)),
                Case($("BarredPostPaid"), Mono.just(ResponseEntity.ok().build())),
                Case($(), () -> Mono.just(ResponseEntity.status(400).build()))
        );
    }

    @Override
    public Mono<ResponseEntity<Object>> otpMessage(String otpId, String statusId, String mobile) {
        if(mobile.length() < 11)
            mobile = "0" + mobile;
        final String phone = mobile;
        return Option.of(statusId)
                .filter(statId -> statId.equals("48"))
                .map(statId -> commandService.subscribeOtpMessage(otpId,phone))
                .getOrElse(() -> Mono.just(ResponseEntity.ok().build()));
    }

    @Override
    public Mono<ResponseEntity<Object>> subscribePushOtp(String mobile) {
        if(mobile.length() < 11)
            mobile = "0" + mobile;
        if(!mobile.startsWith("09") || mobile.length() != 11)
            return Mono.just(ResponseEntity.status(400).body("invalid mobile number"));
        return userRepository.findByPhoneNumber(mobile)
                .switchIfEmpty(Mono.just(new User(mobile)))
                .filter(user -> !user.getHasSubscribed())
                .switchIfEmpty(Mono.error(new AlreadySubscribedException("already subscribed")))
                .flatMap(user ->
                        imiService.sendPushOtpRequest(user.getPhoneNumber(),5)
                        .flatMap(otpId -> {
                            user.setOtpId(otpId);
                            return userRepository.save(user);
                        })
                )
                .flatMap(user -> Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(throwable -> {
                           if(throwable instanceof AlreadySubscribedException)
                               return Mono.just(ResponseEntity.accepted().build());
                           else if(throwable instanceof BadRequestException)
                               return Mono.just(ResponseEntity.badRequest().build());
                           else return Mono.just(ResponseEntity.status(500).build());
                        }
                );
    }

    @Override
    public Mono<ResponseEntity<Object>> unsubscribePushOtp(String mobile) {
        if(mobile.length() < 11)
            mobile = "0" + mobile;
        if(!mobile.startsWith("09") || mobile.length() != 11)
            return Mono.just(ResponseEntity.status(400).body("invalid mobile number"));
        return userRepository.findByPhoneNumber(mobile)
                .switchIfEmpty(Mono.just(new User(mobile)))
                .filter(user -> user.getHasSubscribed())
                .switchIfEmpty(Mono.error(new AlreadyUnsubscribedException("already unsubscribed")))
                .flatMap(user -> imiService.sendPushOtpRequest(user.getPhoneNumber(),6))
                .flatMap(result -> Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(throwable -> {
                    if(throwable instanceof AlreadyUnsubscribedException)
                        return Mono.just(ResponseEntity.accepted().build());
                    else if(throwable instanceof BadRequestException)
                        return Mono.just(ResponseEntity.badRequest().build());
                    else return Mono.just(ResponseEntity.status(500).build());
                });
    }
}
