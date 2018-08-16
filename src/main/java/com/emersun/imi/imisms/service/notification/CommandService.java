package com.emersun.imi.imisms.service.notification;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface CommandService {

    Mono<ResponseEntity<Object>> subscription(NotificationDto notificationDto);
    Mono<ResponseEntity<Object>> unsubscription(NotificationDto notificationDto);
    Mono<ResponseEntity<Object>> renewal(NotificationDto notificationDto);
    Mono<ResponseEntity<Object>> failedRenewal(NotificationDto notificationDto);

    Mono<ResponseEntity<Object>> subscribeOtpMessage(String otpId, String phone);
}
