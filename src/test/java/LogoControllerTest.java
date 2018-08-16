import org.junit.Test;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

public class LogoControllerTest extends AbstractMongodbPurging {
    private String logoApiAddress = "http://localhost:8083/api/v1/logo/download";

    @Test
    public void downloadLogoResource_success() {
        given().header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .get(logoApiAddress)
                .then().statusCode(200);
    }
}
