package com.emersun.imi.imisms.service;

import com.emersun.imi.panel.dto.SubscribeDto;
import com.emersun.imi.repositories.SubscribeLogRepository;
import com.emersun.imi.repositories.UserRepository;
import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Optional;

@Service
public class SubscribeLogServiceImpl implements SubscribeLogService {
    @Autowired
    private SubscribeLogRepository subscribeLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Flux<SubscribeDto> getSubsribeLogs(Pageable pageable, Optional<String> mobileNumber) {
        return Option.ofOptional(mobileNumber)
                .filter(mobile -> !mobile.isEmpty())
                .map(mobile -> userRepository.findByPhoneNumber(mobile)
                        .flux()
                        .flatMap(user -> subscribeLogRepository.findByUserId(user.getId(),pageable)))
                .getOrElse(() -> subscribeLogRepository.findAll()
                        .skip(pageable.getPageNumber()*pageable.getPageSize())
                        .take(pageable.getPageSize())).map(SubscribeDto::new)
                .sort(Comparator.comparing(SubscribeDto::getCreatedAt).reversed());
    }

    @Override
    public Mono<Long> totalNumberOfElements(Pageable pageable,Optional<String> mobileNumber) {
        return Option.ofOptional(mobileNumber)
                .filter(mobile -> !mobile.isEmpty())
                .map(mobile -> userRepository.findByPhoneNumber(mobile)
                        .flatMap(user -> subscribeLogRepository.countByUserId(user.getId())))
                .getOrElse(() -> subscribeLogRepository.count());
    }
}