package com.emersun.imi.panel.controller;

import com.emersun.imi.panel.dto.ChangePasswordDto;
import com.emersun.imi.panel.dto.DailyReport;
import com.emersun.imi.panel.dto.UserAccountDto;
import com.emersun.imi.panel.service.UserAccountService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;

//@CrossOrigin(origins = "*", allowedHeaders = "*",
//        methods = {RequestMethod.DELETE,RequestMethod.GET,
//                RequestMethod.OPTIONS,RequestMethod.POST,
//                RequestMethod.PUT})
@RestController
@RequestMapping("${api.base.url}" + "/admin/accounts")
public class AdminUserAccountController {
    @Autowired
    private UserAccountService userAccountService;

    @ApiOperation(value = "Create a new manager(operator) for admin panel with role of OPERATOR", response = UserAccountDto.class,
            notes = "Only users with ADMIN role are authorized to call this api")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public Mono<ResponseEntity<UserAccountDto>> createUserAccount(@Validated @RequestBody UserAccountDto userAccountDto) {
        return userAccountService.createUserAccount(userAccountDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public Mono<ResponseEntity<UserAccountDto>> editUserAccount(@Validated @RequestBody UserAccountDto userAccountDto) {
        return userAccountService.editUserAccount(userAccountDto);
    }

    @ApiOperation(value = "Delete a manager(operator) with manager's id",
            notes = "Only users with ADMIN role are authorized to call this api")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteUserAccount(@PathVariable String id) {
        return userAccountService.deleteUserAccount(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @PostMapping("/change-password")
    public Mono<ResponseEntity<Object>> changePassword(@Validated @RequestBody ChangePasswordDto changePasswordDto,Principal principal) {
        return userAccountService.changeCurrentUserPassword(changePasswordDto,principal.getName());
    }

    @ApiOperation(value = "Use this api to get list of all panel's managers",
            notes = "Only users with ADMIN role are authorized to call this api")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/operators")
    public Flux<UserAccountDto> listOfOperators(@RequestParam(required = false) Optional<Integer> page,
                                                @RequestParam(required = false) Optional<Integer> size) {
        return userAccountService.listOfOperators(page.orElse(0),size.orElse(10));
    }

    @ApiOperation(value = "use this api to get a manager by it's id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/operators/{id}")
    public Mono<UserAccountDto> getOperatorById(@PathVariable String id) {
        return userAccountService.getOperatorById(id);
    }
}
