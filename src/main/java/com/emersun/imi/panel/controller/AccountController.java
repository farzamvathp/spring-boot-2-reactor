package com.emersun.imi.panel.controller;

import com.emersun.imi.collections.Permission;
import com.emersun.imi.panel.dto.LoginDto;
import com.emersun.imi.panel.service.UserAccountService;
import com.emersun.imi.security.TokenModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${api.base.url}")
public class AccountController {
    @Autowired
    private UserAccountService userAccountService;

    @ApiOperation(value = "Login rest api",response = TokenModel.class)
    @PostMapping("/login")
    public Mono<ResponseEntity<TokenModel>> login(@Validated @RequestBody LoginDto loginDto) {
        return userAccountService.login(loginDto);
    }

    @ApiOperation(value = "refresh token api", response = TokenModel.class)
    @GetMapping("/refresh")
    public Mono<ResponseEntity<TokenModel>> refresh(@RequestParam String refresh) {
        return userAccountService.refresh(refresh);
    }

    @GetMapping("/permissions")
    public Flux<Permission> permissions() {
        return Flux.fromArray(Permission.values());
    }
}
