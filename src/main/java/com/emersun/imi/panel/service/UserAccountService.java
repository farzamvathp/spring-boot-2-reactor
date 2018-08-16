package com.emersun.imi.panel.service;

import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.panel.dto.ChangePasswordDto;
import com.emersun.imi.panel.dto.LoginDto;
import com.emersun.imi.panel.dto.UserAccountDto;
import com.emersun.imi.security.TokenModel;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserAccountService {
    Mono<ResponseEntity<TokenModel>> login(LoginDto loginDto);

    Mono<ResponseEntity<TokenModel>> refresh(String refreshToken);

    Mono<ResponseEntity<UserAccountDto>> createUserAccount(UserAccountDto userAccountDto);
    Mono<ResponseEntity<UserAccountDto>> editUserAccount(UserAccountDto userAccountDto);
    Mono<ResponseEntity<Object>> changeCurrentUserPassword(ChangePasswordDto changePasswordDto, String currentUsername);

    Mono<UserAccount> checkCurrentUserPermissionsBasedOnQueryType(String username, String queryType);

    Flux<UserAccountDto> listOfOperators(Integer page, Integer size);

    Mono<UserAccountDto> getOperatorById(String id);

    Mono<ResponseEntity<String>> deleteUserAccount(String id);
}
