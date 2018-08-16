import com.emersun.imi.collections.ReportType;
import com.emersun.imi.collections.SubscribeLog;
import com.emersun.imi.collections.User;
import com.emersun.imi.configs.MongodbUtil;
import com.emersun.imi.configs.VASProperties;
import com.emersun.imi.exceptions.BaseException;
import com.emersun.imi.imisms.service.IMIService;
import com.emersun.imi.repositories.ReportRepository;
import com.emersun.imi.repositories.SubscribeLogRepository;
import com.emersun.imi.repositories.UserRepository;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class VasApiIntegTest extends AbstractMongodbPurging {
    @Autowired
    private VASProperties vasProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscribeLogRepository subscribeLogRepository;
    @Autowired
    private ReportRepository reportRepository;
    @MockBean
    private IMIService imiService;
    @Autowired
    private MongodbUtil mongodbUtil;

    private final String serverAddress = "http://localhost:8083/api/v1/vas";

    @Test
    public void messageApi_unauthorized() {
        given().get(new StringBuilder(serverAddress)
                .append("/message?text=")
                .append("salam")
                .append("&smsId=")
                .append("smsId")
                .append("&to=")
                .append("09121111111")
                .append("&from=")
                .append("09124337522")
                .append("&userid=")
                .append(614)
                .toString())
                .then().statusCode(401);
    }

    @Test
    public void messageApi_accepted() {
        given().get(new StringBuilder(serverAddress)
                .append("/message?text=")
                .append("Subscription")
                .append("&smsId=")
                .append("smsId")
                .append("&to=")
                .append("09121111111")
                .append("&from=")
                .append("09124337522")
                .append("&userid=")
                .append(vasProperties.getUserid())
                .toString())
                .then().statusCode(202);
    }

    @Test
    public void messageApi_ok() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        given().get(new StringBuilder(serverAddress)
                .append("/message?text=")
                .append("salam chetori")
                .append("&smsId=")
                .append("smsId")
                .append("&to=")
                .append("09121111111")
                .append("&from=")
                .append("09124337522")
                .append("&userid=")
                .append(vasProperties.getUserid())
                .toString())
                .then().statusCode(200);
    }

    @Test
    public void notificationSubscription_ok() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        given().get(new StringBuilder(serverAddress)
                .append("/notification?text=")
                .append("Subscription")
                .append("&keyword=")
                .append("keyword")
                .append("&channel=")
                .append("channel")
                .append("&NotificationId=")
                .append("notificationId")
                .append("&userid=")
                .append(vasProperties.getUserid().toString())
                .append("&to=")
                .append("09121111111")
                .append("&from=")
                .append("09124765214")
                .append("&slsserviceid=658")
                .toString())
                .then().statusCode(200);
    }

    @Test
    public void notificationSubscriptionNewUser_sendIMIRequest_fail() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),
                Mockito.anyString()))
                .thenThrow(new BaseException("error in send imi request 400 status"));
        given().get(new StringBuilder(serverAddress)
                .append("/notification?text=")
                .append("Subscription")
                .append("&keyword=")
                .append("keyword")
                .append("&channel=")
                .append("channel")
                .append("&NotificationId=")
                .append("notificationId")
                .append("&userid=")
                .append(vasProperties.getUserid().toString())
                .append("&to=")
                .append("09121111111")
                .append("&from=")
                .append("09124337522")
                .append("&slsserviceid=658")
                .toString())
                .then().statusCode(400);
        assertThat(userRepository.findByPhoneNumber("09124337522").block().getHasSubscribed()).isEqualTo(true);
        assertThat(subscribeLogRepository.count().block()).isEqualTo(1);
        assertThat(reportRepository.findByType(ReportType.VAS_SUBSCRIBE.name()).block().getCount()).isEqualTo(0);
    }

    @Test
    public void barredPostPaid_success() {
        given().get(new StringBuilder(serverAddress)
                .append("/notification?text=")
                .append("BarredPostPaid")
                .append("&keyword=")
                .append("keyword")
                .append("&channel=")
                .append("channel")
                .append("&NotificationId=")
                .append("notificationId")
                .append("&userid=")
                .append(vasProperties.getUserid().toString())
                .append("&to=")
                .append("09121111111")
                .append("&from=")
                .append("09124337522")
                .append("&slsserviceid=658")
                .toString())
                .then().statusCode(200);
    }


    @Test
    public void notificationFromHumSimulation() {
        Mockito.when(imiService.sendIMIRequest(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok().build()));
        given().get(serverAddress +
                "/notification?text=Unsubscription&keyword=Off1&channel=SMS&from=9124765214&to=405512&NotificationId=130610270&userid=57397&slsserviceid=658")
                .then().statusCode(200);
    }
}
