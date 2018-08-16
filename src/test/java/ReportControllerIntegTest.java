import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.SubscribeLog;
import com.emersun.imi.collections.SubscribeType;
import com.emersun.imi.collections.User;
import com.emersun.imi.repositories.SubscribeLogRepository;
import com.emersun.imi.repositories.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class ReportControllerIntegTest extends AbstractMongodbPurging {

    private final String serverAddress = "http://localhost:8083/api/v1/admin/reports";

    @Autowired
    private SubscribeLogRepository subscribeLogRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void detailedReports_success() {
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
        given().header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .get(serverAddress + "/detailed/" + Permission.CURRENT_VAS_SUB.name() + "/?page=0")
                .then().statusCode(200)
                .body("content[0].count",is(6))
                .body("hasNext",is(false))
                .body("currentPage",is(0));

    }

    @Test
    public void subscribeLog_success() {
        subscribeLogRepository.saveAll(Arrays.asList(
                new SubscribeLog("channel", "renewal","2","3","09124337522"),
                new SubscribeLog("channel", "renewal","2","3","09124337522"),
                new SubscribeLog("channel", "renewal","2","3","09124337522"),
                new SubscribeLog("channel", "renewal","2","3","09124337522"),
                new SubscribeLog("channel", "renewal","2","3","09124337522")
        )).blockLast();
        given().header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .get(serverAddress + "/subscribe_log/")
                .then().statusCode(200).body("content.size()",is(5));
    }

    @Test
    public void subscribeLogUser_success() {
        User user = userRepository.save(new User("09129090219")).block();
        subscribeLogRepository.saveAll(Arrays.asList(
                new SubscribeLog("channel", "renewal",user.getId(),"3","09124337522"),
                new SubscribeLog("channel", "renewal",user.getId(),"3","09124337522"),
                new SubscribeLog("channel", "renewal","2","3","09124337522"),
                new SubscribeLog("channel", "renewal","2","3","09124337522"),
                new SubscribeLog("channel", "renewal","2","3","09124337522")
        )).blockLast();
        given().header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .get(serverAddress + "/subscribe_log?mobile=09129090219")
                .then().statusCode(200).body("content.size()",is(2));
    }
}
