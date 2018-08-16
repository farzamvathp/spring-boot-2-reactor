package com.emersun.imi.repositories;

import com.emersun.imi.collections.SubscribeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface SubscribeLogRepository extends ReactiveCrudRepository<SubscribeLog,String> {
    Flux<SubscribeLog> findByUserId(String userId, Pageable pageable);
    Mono<Long> countByUserId(String userId);
    Flux<SubscribeLog> findByTypeAndCreatedAtAfter(String type, LocalDateTime localDateTime);
    Flux<SubscribeLog> findByType(String type);
    Flux<SubscribeLog> findByTypeAndChannelAndCreatedAtAfter(String type,String channel,LocalDateTime localDateTime);
    Flux<SubscribeLog> findByTypeAndChannel(String type,String channel);
}
