import com.emersun.imi.panel.dto.ChangePasswordDto;
import org.junit.Test;
import org.springframework.http.MediaType;


import static io.restassured.RestAssured.given;

public class UserAccountControllerTest extends AbstractMongodbPurging {

    private final String serverAddress = "http://localhost:8083/api/v1/admin/accounts";
    @Test
    public void changePassword_success() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword("imiadmin");
        changePasswordDto.setRetryNewPassword("imi");
        changePasswordDto.setNewPassword("imi");
        given().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("X-AUTH-TOKEN",getToken())
                .body(changePasswordDto)
                .post(serverAddress + "/change-password/")
                .then().statusCode(200);
    }
}
