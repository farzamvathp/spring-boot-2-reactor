package com.emersun.imi.imisms.service;

import com.emersun.imi.panel.dto.SubscribeDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface SubscribeLogService {
    Flux<SubscribeDto> getSubsribeLogs(Pageable pageable, Optional<String> mobileNumber);

    Mono<Long> totalNumberOfElements(Pageable pageable, Optional<String> mobileNumber);
}
