import com.emersun.imi.collections.ReportType;
import com.emersun.imi.collections.User;
import com.emersun.imi.configs.VASProperties;
import com.emersun.imi.exceptions.BaseException;
import com.emersun.imi.imisms.service.IMIService;
import com.emersun.imi.imisms.service.notification.CommandService;
import com.emersun.imi.imisms.service.notification.NotificationDto;
import com.emersun.imi.panel.dto.OveralReportDto;
import com.emersun.imi.panel.service.ReportService;
import com.emersun.imi.repositories.ReportRepository;
import com.emersun.imi.repositories.SubscribeLogRepository;
import com.emersun.imi.repositories.UserRepository;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandServiceIntegTest extends AbstractMongodbPurging {
    @Autowired
    private CommandService commandService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscribeLogRepository subscribeLogRepository;
    @Autowired
    private ReportRepository reportRepository;
    @MockBean
    private IMIService imiService;
    @Autowired
    private VASProperties vasProperties;
    @Autowired
    private ReportService reportService;

    @Test
    public void subscriptionOtpChannelOldUser_success() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        userRepository.save(new User(false,"09124337522")).block();
        assertThat(commandService.subscribeOtpMessage("1234","09124337522").block().getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByPhoneNumber("09124337522").block().getHasSubscribed())
                .isEqualTo(true);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()).block().getCount())
                .isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_OTP_SUBSCRIBE.name()).block().getCount())
                .isEqualTo(1);
        assertThat(userRepository.countByHasSubscribed(true).block())
                .isEqualTo(1);
        OveralReportDto reportDto = reportService.getVasOveralReport("salam").block();
        assertThat(reportDto.getVasSubCount()).isEqualTo(1);
        assertThat(reportDto.getVasOTPSubCount()).isEqualTo(1);
        reportService.monthlySubscriptionInfo(0,"otp_subscribe").blockFirst();
    }

    @Test
    public void subscriptionOtpChannelNewUser_success() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        assertThat(commandService.subscribeOtpMessage("1234","09124337522").block().getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByPhoneNumber("09124337522").block().getHasSubscribed())
                .isEqualTo(true);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()).block().getCount())
                .isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_OTP_SUBSCRIBE.name()).block().getCount())
                .isEqualTo(1);
    }

    @Test
    public void subscriptionOtpChannelOldUser_fail() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),
                Mockito.anyString()))
                .thenThrow(new BaseException("error in send imi request 400 status"));
        userRepository.save(new User(false,"09124337522")).block();

        assertThat(commandService.subscribeOtpMessage("1234","09124337522").block().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(userRepository.findByPhoneNumber("09124337522").block().getHasSubscribed()).isEqualTo(true);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()).block().getCount()).isEqualTo(0);
        assertThat(reportRepository.findByType(ReportType.VAS_OTP_SUBSCRIBE.name()).block().getCount()).isEqualTo(0);
    }

    @Test
    public void subscriptionOldUser_success() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        userRepository
                .save(new User(false,"09124337522"))
                .block();
        NotificationDto notificationDto =
                new NotificationDto("Subscription",
                        "keyword",
                        "channel","notificationId",vasProperties.getUserid().toString(),"09124337822","09124337522");
        assertThat(commandService.subscription(notificationDto).block().getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(userRepository.findByPhoneNumber("09124337522").block().getHasSubscribed()).isEqualTo(true);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()).block().getCount()).isEqualTo(1);
    }

    @Test
    public void subcriptionOldUserOtpChannel_success() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        userRepository
                .save(new User(false,"09124337522"))
                .block();
        NotificationDto notificationDto =
                new NotificationDto("Subscription",
                        "keyword",
                        "OTP","notificationId",vasProperties.getUserid().toString(),"09124337822","09124337522");
        assertThat(commandService.subscription(notificationDto).block().getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    public void subscriptionNewUser_success() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        NotificationDto notificationDto =
                new NotificationDto("Subscription",
                        "keyword",
                        "channel","notificationId",vasProperties.getUserid().toString(),"09124337822","09124337522");
        assertThat(commandService.subscription(notificationDto).block().getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(userRepository.findByPhoneNumber("09124337522").block().getHasSubscribed()).isEqualTo(true);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()).block().getCount()).isEqualTo(1);
    }

    @Test
    public void subscriptionNewUser_sendIMIRequest_fail() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),
                Mockito.anyString()))
                .thenThrow(new BaseException("error in send imi request 400 status"));
        NotificationDto notificationDto =
                new NotificationDto("Subscription",
                        "keyword",
                        "channel","notificationId",vasProperties.getUserid().toString(),"09124337822","09124337522");
        assertThat(commandService.subscription(notificationDto).block().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(userRepository.findByPhoneNumber("09124337522").block().getHasSubscribed()).isEqualTo(true);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()).block().getCount()).isEqualTo(0);
    }

    @Test
    public void renewalOldUser_success() {
        userRepository
                .save(new User(true,"09124337522"))
                .block();
        NotificationDto notificationDto =
                new NotificationDto("Renewal",
                        "keyword",
                        "channel","notificationId",vasProperties.getUserid().toString(),"09124337822","09124337522");
        assertThat(commandService.renewal(notificationDto).block().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByPhoneNumber("09124337522").block().getRenewalCount()).isEqualTo(1);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUCCESS_CHARGE.name()).block().getCount()).isEqualTo(1);
    }

    @Test
    public void failedRenewal_success() {
        NotificationDto notificationDto =
                new NotificationDto("FailedRenewal",
                        "keyword",
                        "channel","notificationId",vasProperties.getUserid().toString(),"09124337822","09124337522");
        assertThat(commandService.failedRenewal(notificationDto).block().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
    }
}
