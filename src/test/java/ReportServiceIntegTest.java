import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.SubscribeLog;
import com.emersun.imi.collections.SubscribeType;
import com.emersun.imi.collections.User;
import com.emersun.imi.panel.service.ReportService;
import com.emersun.imi.panel.service.UserAccountService;
import com.emersun.imi.repositories.ReportRepository;
import com.emersun.imi.repositories.SubscribeLogRepository;
import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.repositories.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportServiceIntegTest extends AbstractMongodbPurging {

    @Autowired
    private ReportService reportService;
    @Autowired
    private SubscribeLogRepository subscribeLogRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void search_success_subscribe() {
        User[] users = {
                new User(true,"09124336520"),
                new User(true,"09124336521"),
                new User(true,"09124336522"),
                new User(true,"09124336523"),
                new User(true,"09124336524"),
                new User(true,"09124336525"),
                new User(false,"09124336526"),
                new User(false,"09124336527")
        };
        Flux.fromArray(users).collect(Collectors.toList())
                .flux()
                .flatMap(userRepository::saveAll).subscribe();
        SubscribeLog[] logs = {
                new SubscribeLog("SMS","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(1),
                        SubscribeType.SUBSCRIBE.getName()),
                new SubscribeLog("SMS","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(5),
                        SubscribeType.SUBSCRIBE.getName()),
                new SubscribeLog("SMS","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(2),
                        SubscribeType.SUBSCRIBE.getName()),
                new SubscribeLog("SMS","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(3),
                        SubscribeType.SUBSCRIBE.getName()),
                new SubscribeLog("SMS","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(4),
                        SubscribeType.SUBSCRIBE.getName()),
                new SubscribeLog("OTP","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(4),
                        SubscribeType.SUBSCRIBE.getName()),
                // 40 days before
                new SubscribeLog("OTP","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(40),
                        SubscribeType.SUBSCRIBE.getName()),
                new SubscribeLog("OTP","1","2",
                        LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(14),
                        SubscribeType.SUBSCRIBE.getName()) };
        Flux.fromArray(logs).collect(Collectors.toList())
                .flux()
                .flatMap(subscribeLogRepository::saveAll).subscribe();

        assertThat(reportService.getMonthlyReportsBasedOnPermission(0,Permission.CURRENT_VAS_SUB.name(),adminUsername)
                .collect(Collectors.toList()).block().get(0).getCount())
                .isEqualTo(6);

        assertThat(reportService.getMonthlyReportsBasedOnPermission(0,Permission.CURRENT_VAS_UNSUB.name(),adminUsername)
                .collect(Collectors.toList()).block().get(0).getCount())
                .isEqualTo(2);

        assertThat(reportService.getMonthlyReportsBasedOnPermission(0, Permission.SMS_SUB.name(),adminUsername)
                .collect(Collectors.toList()).block().size())
                .isEqualTo(5);

        assertThat(reportService.getMonthlyReportsBasedOnPermission(0,Permission.OTP_SUB.name(),adminUsername)
                .collect(Collectors.toList()).block().size())
                .isEqualTo(2);


        assertThat(reportService.getMonthlyReportsBasedOnPermission(0,Permission.VAS_SUB.name(),adminUsername)
                .collect(Collectors.toList()).block().size())
                .isEqualTo(6);

        assertThat(reportService.getMonthlyReportsBasedOnPermission(0,Permission.VAS_UNSUB.name(),adminUsername)
                .collect(Collectors.toList()).block().size())
                .isEqualTo(0);
    }
}
