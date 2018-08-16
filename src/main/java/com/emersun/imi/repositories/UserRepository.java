package com.emersun.imi.repositories;

import com.emersun.imi.collections.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User,String> {
    Mono<User> findByPhoneNumber(String phoneNumber);
    Mono<Long> countByHasSubscribed(Boolean hasSubscribed);
    Flux<User> findByHasSubscribed(Boolean hasSubscribed);
}
