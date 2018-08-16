package com.emersun.imi.repositories;

import com.emersun.imi.collections.Report;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReportRepository extends ReactiveCrudRepository<Report,String> {
    Mono<Report> findByType(String type);
    Mono<Long> countByType(String type);
}
