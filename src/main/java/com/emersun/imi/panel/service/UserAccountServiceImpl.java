package com.emersun.imi.panel.service;

import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.configs.Constants;
import com.emersun.imi.exceptions.BaseException;
import com.emersun.imi.exceptions.UnauthorizedException;
import com.emersun.imi.panel.dto.ChangePasswordDto;
import com.emersun.imi.panel.dto.LoginDto;
import com.emersun.imi.panel.dto.UserAccountDto;
import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.security.JwtTokenProvider;
import com.emersun.imi.security.TokenModel;
import com.emersun.imi.utils.Messages;
import com.emersun.imi.utils.messages.Response;
import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private static final Logger logger = LoggerFactory.getLogger(UserAccountServiceImpl.class);
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Messages messages;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<ResponseEntity<TokenModel>> login(LoginDto loginDto) {
        return userAccountRepository.findByUsername(loginDto.getUsername())
                .map(userAccount -> Option.of(userAccount).filter(Objects::nonNull).getOrElseThrow(() -> new BaseException(messages.get(Response.USERNAME_NOT_FOUND))))
                .filter(userAccount -> passwordEncoder.matches(loginDto.getPassword(),userAccount.getPassword()))
                .switchIfEmpty(Mono.error(new BaseException("incorrect password")))
                .flatMap(userAccount -> Mono.just(ResponseEntity.ok(jwtTokenProvider.createToken(userAccount.getUsername(), Optional.empty(),userAccount.getPermissions()))))
                .doOnError(throwable -> logger.error("Error in creating JWT token : ",throwable));
    }

    @Override
    public Mono<ResponseEntity<TokenModel>> refresh(String refreshToken) {
        return Mono.just(refreshToken)
                .filter(refresh -> jwtTokenProvider.validateToken(refreshToken, Constants.REFRESH_TOKEN_AUDIENCE))
                .map(refresh -> jwtTokenProvider.getUsername(refresh))
                .flatMap(username -> userAccountRepository.findByUsername(username))
                .map(userAccount -> jwtTokenProvider.createToken(userAccount.getUsername(),Optional.of(refreshToken),userAccount.getPermissions()))
                .flatMap(tokenModel -> Mono.just(ResponseEntity.ok(tokenModel)))
                .onErrorResume(throwable -> {
                    if(throwable instanceof UnauthorizedException) {
                        return Mono.just(ResponseEntity.status(401).build());
                    } else {
                        return Mono.just(ResponseEntity.status(500).build());
                    }
                });
    }

    @Override
    public Mono<ResponseEntity<UserAccountDto>> createUserAccount(UserAccountDto userAccountDto) {
        return userAccountRepository.findByUsername(userAccountDto.getUsername())
                .map(userAccount -> Option.of(userAccount).filter(Objects::isNull).getOrElseThrow(() -> new BaseException(messages.get(Response.ACCOUNT_ALREADY_EXISTS))))
                .switchIfEmpty(Mono.just(new UserAccount(userAccountDto)))
                .doOnSuccess(userAccount -> userAccount.setPassword(passwordEncoder.encode(userAccountDto.getPassword())))
                .flatMap(userAccountRepository::save)
                .flatMap(userAccount -> Mono.just(ResponseEntity.ok(new UserAccountDto(userAccount))));
    }

    @Override
    public Mono<ResponseEntity<UserAccountDto>> editUserAccount(UserAccountDto userAccountDto) {
        return userAccountRepository.findByUsername(userAccountDto.getUsername())
                .switchIfEmpty(Mono.error(new BaseException(messages.get(Response.USERNAME_NOT_FOUND))))
                .map(userAccount -> userAccount.edit(userAccountDto))
                .flatMap(userAccountRepository::save)
                .flatMap(userAccount -> Mono.just(ResponseEntity.ok(new UserAccountDto(userAccount))));
    }

    @Override
    public Mono<ResponseEntity<Object>> changeCurrentUserPassword(ChangePasswordDto changePasswordDto, String currentUsername) {
        return Mono.just(changePasswordDto)
                .filter(dto -> dto.getNewPassword().equals(dto.getRetryNewPassword()))
                .switchIfEmpty(Mono.error(new BaseException(messages.get(Response.NEW_RETRY_PASS_NOTEQUAL))))
                .filter(dto -> !dto.getNewPassword().equals(dto.getCurrentPassword()))
                .switchIfEmpty(Mono.error(new BaseException(messages.get(Response.OLD_NEW_PASS_EQUALS))))
                .flatMap(dto -> userAccountRepository.findByUsername(currentUsername))
                .map(userAccount -> Option.of(userAccount).filter(Objects::nonNull).getOrElseThrow(() -> new BaseException(messages.get(Response.USERNAME_NOT_FOUND))))
                .filter(userAccount -> passwordEncoder.matches(changePasswordDto.getCurrentPassword(),userAccount.getPassword()))
                .switchIfEmpty(Mono.error(new BaseException("password not match")))
                .doOnSuccess(userAccount -> userAccount.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword())))
                .flatMap(userAccountRepository::save)
                .flatMap(userAccount -> Mono.just(ResponseEntity.ok().build()))
                .doOnError(throwable -> logger.error("Error in subscription method : ",throwable));
    }

    @Override
    public Mono<UserAccount> checkCurrentUserPermissionsBasedOnQueryType(String username,String queryType) {
        return userAccountRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(messages.get(Response.USERNAME_NOT_FOUND))))
                .filter(userAccount -> userAccount.getPermissions().contains(Enum.valueOf(Permission.class,queryType)))
                .switchIfEmpty(Mono.error(new UnauthorizedException(messages.get(Response.ACCESS_DENIED))));
    }

    @Override
    public Flux<UserAccountDto> listOfOperators(Integer page, Integer size) {
        return userAccountRepository.findAll()
                .skip(page*size).take(size)
                .map(UserAccountDto::new);
    }

    @Override
    public Mono<UserAccountDto> getOperatorById(String id) {
        return userAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new BaseException(messages.get(Response.USERNAME_NOT_FOUND))))
                .map(UserAccountDto::new);
    }

    @Override
    public Mono<ResponseEntity<String>> deleteUserAccount(String id) {
        return userAccountRepository.deleteById(id)
                .flatMap(aVoid -> Mono.just(ResponseEntity.ok(messages.get(Response.SUCCESS))))
                .doOnError(throwable -> logger.error("Error in deleting user account : ",throwable));
    }
}
