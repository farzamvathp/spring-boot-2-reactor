import com.emersun.imi.Application;
import com.emersun.imi.configs.MongodbUtil;
import com.emersun.imi.security.JwtTokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
public abstract class AbstractMongodbPurging {
    @Autowired
    private MongodbUtil mongodbUtil;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Value("${panel.admin.username}")
    protected String adminUsername;
    @Before
    public void before() {
        mongodbUtil.dropCollections();
        mongodbUtil.createCollections();
        mongodbUtil.insertReports();
        mongodbUtil.insertAdminUserAccount();
    }

    protected String getToken() {
        return "Bearer " + jwtTokenProvider.createAccessToken(adminUsername);
    }
    @Test
    public void sample() {}
}
