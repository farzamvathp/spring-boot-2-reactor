package com.emersun.imi.configs;

import com.emersun.imi.collections.*;
import com.emersun.imi.repositories.ReportRepository;
import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.repositories.UserRepository;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MongodbUtil {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${panel.admin.username}")
    private String adminUsername;
    @Value("${panel.admin.password}")
    private String adminPassword;

    public void dropCollections() {
        mongoOperations.dropCollection(User.class);
        mongoOperations.dropCollection(SubscribeLog.class);
        mongoOperations.dropCollection(Report.class);
        mongoOperations.dropCollection(UserAccount.class);
    }
    public void createCollections() {
        mongoOperations.createCollection(Report.class, CollectionOptions.empty().size(10000000));
        mongoOperations.createCollection(SubscribeLog.class, CollectionOptions.empty().size(10000000));
        mongoOperations.createCollection(User.class, CollectionOptions.empty().size(10000000));
        mongoOperations.createCollection(UserAccount.class, CollectionOptions.empty().size(10000000));
    }

    public void insertReports() {
        Arrays.asList(ReportType.values())
                .forEach(reportType -> {
                    reportRepository.findByType(reportType.name())
                            .switchIfEmpty(Mono.just(new Report(0L,reportType.name())))
                            .flatMap(reportRepository::save)
                            .subscribe();
                });
    }

    public void insertAdminUserAccount() {
        userAccountRepository.findByUsername(adminUsername)
                .map(userAccount -> {
                    userAccount.setCanReadFromDate(userAccount.getCreatedAt().minusYears(1));
                    userAccount.setPermissions(Sets.newHashSet(Permission.values()));
                    return userAccount;
                })
                .switchIfEmpty(Mono.just(new UserAccount(adminUsername,passwordEncoder.encode(adminPassword),
                        false, Constants.ADMIN_ROLE, Sets.newHashSet(Permission.values()))))
                .flatMap(userAccountRepository::save)
                .subscribe();
    }
}
