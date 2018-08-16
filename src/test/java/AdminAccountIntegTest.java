import com.emersun.imi.collections.Permission;
import com.emersun.imi.collections.UserAccount;
import com.emersun.imi.panel.dto.LoginDto;
import com.emersun.imi.panel.dto.UserAccountDto;
import com.emersun.imi.repositories.UserAccountRepository;
import com.emersun.imi.security.TokenModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AdminAccountIntegTest extends AbstractMongodbPurging {

    private final String serverAddress = "http://localhost:8083/api/v1/admin/accounts";
    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper mapper;
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    public void createUser_success() throws JsonProcessingException {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setFirstname("farzam");
        userAccountDto.setLastname("lastname");
        userAccountDto.setMobile("09124337522");
        userAccountDto.setPassword("farzamfarzam");
        userAccountDto.setUsername("farzamvat");
        userAccountDto.setCanReadFromDate(LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(40));
        userAccountDto.setPermissions(Sets.newHashSet(Permission.values()));
        given().header("Accept",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .body(mapper.writeValueAsString(userAccountDto))
                .post(serverAddress)
                .then().statusCode(200)
                .body("role",equalTo("OPERATOR"))
                .body("firstname",equalTo(userAccountDto.getFirstname()))
                .body("lastname",equalTo(userAccountDto.getLastname()))
                .body("mobile",equalTo(userAccountDto.getMobile()))
                .body("username",equalTo(userAccountDto.getUsername()))
                .body("id",notNullValue());
    }

    @Test
    public void create_userAccount_login_delete_userAccount_access_denied() throws JsonProcessingException {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setFirstname("farzam");
        userAccountDto.setLastname("lastname");
        userAccountDto.setMobile("09124337522");
        userAccountDto.setPassword("farzamfarzam");
        userAccountDto.setUsername("farzamvat");
        userAccountDto.setCanReadFromDate(LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(40));
        userAccountDto.setPermissions(Sets.newHashSet(Permission.values()));
        UserAccountDto response = given().header("Accept",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .body(mapper.writeValueAsString(userAccountDto))
                .post(serverAddress)
                .then().statusCode(200).extract().body().as(UserAccountDto.class);

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(userAccountDto.getUsername());
        loginDto.setPassword(userAccountDto.getPassword());
        TokenModel tokenModel = given()
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .body(loginDto)
                .post("http://localhost:8083/api/v1/login")
                .then().statusCode(200).extract().body().as(TokenModel.class);

        given()
                .header("Accept",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("X-AUTH-TOKEN","Bearer "+tokenModel.getAccessToken())
                .delete(serverAddress + "/" + response.getId())
                .then().statusCode(403);
    }

    @Test
    public void create_userAccount_delete_userAccount_success() throws JsonProcessingException {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setFirstname("farzam");
        userAccountDto.setLastname("lastname");
        userAccountDto.setMobile("09124337522");
        userAccountDto.setPassword("farzamfarzam");
        userAccountDto.setUsername("farzamvat");
        userAccountDto.setCanReadFromDate(LocalDateTime.now(ZoneId.of("Asia/Tehran")).minusDays(40));
        userAccountDto.setPermissions(Sets.newHashSet(Permission.values()));
        UserAccountDto response = given().header("Accept",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("Content-Type",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .body(mapper.writeValueAsString(userAccountDto))
                .post(serverAddress)
                .then().statusCode(200).extract().body().as(UserAccountDto.class);

        given()
                .header("Accept",MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .delete(serverAddress + "/" + response.getId())
                .then().statusCode(200);
        assertThat(userAccountRepository.findByUsername(userAccountDto.getUsername()).block()).isNull();
    }

    @Test
    public void getOperators_success() {
        userAccountRepository.saveAll(Arrays.asList(
                new UserAccount("username","pass",false,"OPERATOR"),
                new UserAccount("username1","pass",false,"OPERATOR"),
                new UserAccount("username2","pass",false,"OPERATOR"),
                new UserAccount("username3","pass",false,"OPERATOR"),
                new UserAccount("username4","pass",false,"OPERATOR")
        )).blockLast();

        given().accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .get(serverAddress+"/operators")
                .then().statusCode(200)
                .body("size()",equalTo(6));
    }

}
