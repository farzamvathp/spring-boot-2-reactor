import com.emersun.imi.panel.service.LogoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

public class LogoServiceIntegTest extends AbstractMongodbPurging {

    @Autowired
    private LogoService logoService;

    @Test
    public void loadLogoAsResource() {
        Resource resource = logoService.loadLogoAsResource();
        assertThat(resource).isNotNull();
        assertThat(resource.getFilename()).isEqualTo("shadi.png");
    }
}
