package com.emersun.imi.imisms.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface VasService {
    ResponseEntity<?> message(String text, String smsId, String to, String from, String userid);

    Mono<? extends ResponseEntity<?>> notification(String text, String keyword, String channel, String notificationId, String userid, String to, String from);

    Mono<ResponseEntity<Object>> otpMessage(String otpId, String statusId, String recipient);

    Mono<ResponseEntity<Object>> subscribePushOtp(String mobile);

    Mono<ResponseEntity<Object>> unsubscribePushOtp(String mobile);
}
