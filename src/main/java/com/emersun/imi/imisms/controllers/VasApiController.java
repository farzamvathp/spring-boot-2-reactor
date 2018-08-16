package com.emersun.imi.imisms.controllers;

import com.emersun.imi.collections.SubscribeLog;
import com.emersun.imi.imisms.service.SubscribeLogService;
import com.emersun.imi.imisms.service.VasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;


@RestController
@RequestMapping("${api.base.url}" + "/vas")
public class VasApiController {
    @Autowired
    private VasService vasService;
    @Autowired
    private SubscribeLogService subscribeLogService;

    @GetMapping("/message")
    public ResponseEntity<?> message(@RequestParam String text,
                                     @RequestParam String smsId,
                                     @RequestParam String to,
                                     @RequestParam String from,
                                     @RequestParam String userid) {
        return vasService.message(text,smsId,to,from,userid);
    }

    @GetMapping("/notification")
    public Mono<? extends ResponseEntity<?>> notification(@RequestParam String text,
                             @RequestParam String keyword,
                             @RequestParam String channel,
                             @RequestParam String NotificationId,
                             @RequestParam String userid,
                             @RequestParam String to,
                             @RequestParam String from,
                             @RequestParam(required = false) String slsserviceid) {
        return vasService.notification(text,keyword,channel,NotificationId,userid,to,from);
    }

    @PostMapping("/otp_message")
    public Mono<ResponseEntity<Object>> otp_message(@RequestParam String otpId,
                                         @RequestParam String statusId,
                                         @RequestParam String recipient) {
        return vasService.otpMessage(otpId,statusId,recipient);
    }

    // TODO: 5/27/2018 request body validation
    @PostMapping("/push_otp/subscribe")
    public Mono<ResponseEntity<Object>> subscribe_push_otp(@RequestParam String mobile) {
        return vasService.subscribePushOtp(mobile);
    }

    // TODO: 5/27/2018 request body validation
    @PostMapping("/push_otp/unsubscribe")
    public Mono<ResponseEntity<Object>> unsubscribe_push_otp(@RequestParam String mobile) {
        return vasService.unsubscribePushOtp(mobile);
    }

    // TODO: 5/27/2018 request body validation
    @PostMapping("/charge_otp")
    public ResponseEntity<?> charge_otp() {
        return ResponseEntity.ok().build();
    }

}
