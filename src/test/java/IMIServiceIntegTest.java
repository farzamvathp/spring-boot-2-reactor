import com.emersun.imi.exceptions.BadRequestException;
import com.emersun.imi.imisms.service.IMIService;
import com.emersun.imi.imisms.service.IMIWebClient;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;


public class IMIServiceIntegTest extends AbstractMongodbPurging {
    @Autowired
    private IMIService imiService;
    @MockBean
    private IMIWebClient imiWebClient;
    @Test
    public void sendIMIRequest_success() {
        Mockito.when(imiWebClient.sendXmsRequest(Mockito.anyString()))
                .thenReturn(Mono.just("&lt;xmsresponse&gt; &lt;userid&gt;57397&lt;/userid&gt; &lt;action&gt;PushOtp&lt;/action&gt; &lt;code id=\"0\"&gt;ok&lt;/code&gt; &lt;body&gt; &lt;recipient mobile=\"09124337522\" doerid=\"1\" status=\"40\"&gt; 1234 &lt;/recipient&gt; &lt;/body&gt; &lt;/xmsresponse&gt;"
                ));
        assertThat(imiService.sendIMIRequest("09124337522","salam").block().getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    public void sendIMIRequest_fail4xx() {
        Mockito.when(imiWebClient.sendXmsRequest(Mockito.anyString()))
                .thenReturn(Mono.just("&lt;xmsresponse&gt; &lt;userid&gt;57397&lt;/userid&gt; &lt;action&gt;PushOtp&lt;/action&gt; &lt;code id=\"0\"&gt;ok&lt;/code&gt; &lt;body&gt; &lt;recipient mobile=\"09124337522\" doerid=\"1\" status=\"20\"&gt; 1234 &lt;/recipient&gt; &lt;/body&gt; &lt;/xmsresponse&gt;"
                ));
        assertThat(imiService.sendIMIRequest("09124337522","salam").block().getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void sendPushOtp_success() {
        Mockito.when(imiWebClient.sendXmsRequest(Mockito.anyString()))
                .thenReturn(Mono.just("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                        "    <soap:Body>\n" +
                        "        <XmsRequestResponse xmlns=\"http://tempuri.org/\">\n" +
                        "            <XmsRequestResult xsi:type=\"xsd:string\">&lt;xmsresponse&gt;\n" +
                        "\t\t\t\t&lt;userid&gt;57397&lt;/userid&gt;\n" +
                        "\t\t\t\t&lt;action&gt;pushotp&lt;/action&gt;\n" +
                        "\t\t\t\t&lt;code id=\"0\"&gt;ok&lt;/code&gt;\n" +
                        "\t\t\t\t&lt;body&gt;&lt;recipient mobile=\"9124337522\" doerid=\"0\" status=\"40\"&gt;12607293&lt;/recipient&gt;&lt;/body&gt;\n" +
                        "\t\t\t&lt;/xmsresponse&gt;</XmsRequestResult>\n" +
                        "        </XmsRequestResponse>\n" +
                        "    </soap:Body>\n" +
                        "</soap:Envelope>"
                ));
        assertThat(imiService.sendPushOtpRequest("09124337522",5).block()).isEqualTo("12607293");
    }

    @Test(expected = BadRequestException.class)
    public void sendPushOtp_fail() {
        Mockito.when(imiWebClient.sendXmsRequest(Mockito.anyString()))
                .thenReturn(Mono.just("&lt;xmsresponse&gt; &lt;userid&gt;57397&lt;/userid&gt; &lt;action&gt;PushOtp&lt;/action&gt; &lt;code id=\"0\"&gt;ok&lt;/code&gt; &lt;body&gt; &lt;recipient mobile=\"09124337522\" doerid=\"1\" status=\"20\"&gt; 1234 &lt;/recipient&gt; &lt;/body&gt; &lt;/xmsresponse&gt;"
                ));
        assertThat(imiService.sendPushOtpRequest("09124337522",5).block());
    }
}
