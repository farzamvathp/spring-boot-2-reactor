import com.emersun.imi.Application;
import com.emersun.imi.collections.User;
import com.emersun.imi.configs.VASProperties;
import com.emersun.imi.exceptions.BadRequestException;
import com.emersun.imi.imisms.service.IMIService;
import com.emersun.imi.imisms.service.VasService;
import com.emersun.imi.imisms.service.notification.CommandService;
import com.emersun.imi.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class VasServiceIntegTest extends AbstractMongodbPurging {
    @Autowired
    private VasService vasService;
    @Autowired
    private VASProperties vasProperties;
    @MockBean
    private CommandService commandService;
    @MockBean
    private IMIService imiService;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void message_accepted_success() {
        assertThat(vasService.message("Subscription",
                "sms",
                "09124332211",
                "09124337566",
                vasProperties.getUserid().toString()).getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    public void otpMessage_statusNotEqual48() {
        assertThat(vasService.otpMessage("1234","46","09124337522").block().getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    public void subscriptionPatternMatching_success() {
        Mockito.when(commandService.subscription(Mockito.any())).thenReturn(Mono.just(ResponseEntity.ok().build()));
        assertThat(vasService.notification("Subscription",
                "keyword",
                "channel",
                "notificationId",
                vasProperties.getUserid().toString(),
                "09121111111",
                "09122222222").block()).isEqualTo(ResponseEntity.ok().build());
    }

    @Test
    public void unsubscriptionPatternMatching_fail() {
        Mockito.when(commandService.unsubscription(Mockito.any())).thenReturn(Mono.just(ResponseEntity.status(400).build()));
        assertThat(vasService.notification("Unsubscription",
                "keyword",
                "channel",
                "notificationId",
                vasProperties.getUserid().toString(),
                "09121111111",
                "09122222222").block()).isEqualTo(ResponseEntity.status(400).build());
    }

    @Test
    public void defaultPatternMatching_success() {
        assertThat(vasService.notification("default",
                "keyword",
                "channel",
                "notificationId",
                vasProperties.getUserid().toString(),
                "09121111111",
                "09122222222").block()).isEqualTo(ResponseEntity.status(400).build());
    }

    @Test
    public void subscribePushOtp_oldUser_unsubscribed_imiPushEmptyResult_success() {
        userRepository
                .save(new User(false,"09124337522"))
                .block();
        Mockito.when(imiService.sendPushOtpRequest(Mockito.anyString(),Mockito.anyInt()))
                .thenReturn(Mono.error(new BadRequestException("bad request dadash")));
        assertThat(vasService.subscribePushOtp("09124337522").block().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void subscribePushOtp_oldUser_unsubscribed_imiPushOtpResult_success() {
        userRepository
                .save(new User(false,"09124337522"))
                .block();
        Mockito.when(imiService.sendPushOtpRequest(Mockito.anyString(),Mockito.anyInt()))
                .thenReturn(Mono.just("1234"));
        assertThat(vasService.subscribePushOtp("09124337522").block().getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByPhoneNumber("09124337522").block().getOtpId()).isEqualTo("1234");
    }

    @Test
    public void subscribePushOtp_oldUser_subscribed_success() {
        userRepository
                .save(new User(true,"09124337522"))
                .block();
        assertThat(vasService.subscribePushOtp("09124337522").block().getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }
}
