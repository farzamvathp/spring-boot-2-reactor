package com.emersun.imi.imisms.service;

import io.vavr.control.Try;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IMIService {
    Mono<ResponseEntity<Object>> sendIMIRequest(String recipient, String text);

    Try<String> sendChargeOtpRequest(String recipient, String pin, Integer otpid);

    Mono<String> sendPushOtpRequest(String recipient, Integer cost);
}
