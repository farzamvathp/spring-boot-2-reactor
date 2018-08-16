package com.emersun.imi.imisms.service;

import com.emersun.imi.configs.Constants;
import com.emersun.imi.configs.VASProperties;
import com.emersun.imi.exceptions.BadRequestException;
import com.emersun.imi.exceptions.BaseException;
import com.emersun.imi.imisms.dto.*;
import com.emersun.imi.utils.Messages;
import com.emersun.imi.utils.messages.Response;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class IMIServiceImpl implements IMIService {

    private final static Logger log = LoggerFactory.getLogger(IMIServiceImpl.class);
    @Autowired
    private VASProperties vasProperties;
    @Autowired
    private IMIWebClient imiWebClient;
    @Autowired
    private XmlMapper xmlMapper;
    @Autowired
    private Messages messages;

    public Mono<ResponseEntity<Object>> sendIMIRequest(String recipient, String text) {

        Map<String,Object> map = new HashMap<String,Object>() {{
            this.put("action",Action.SMSSEND.getValue());
            this.put("password",vasProperties.getPassword());
            this.put("userid",vasProperties.getUserid());
            this.put("body",new BaseBody(vasProperties.getServiceid(),
                    new Recipient(recipient,vasProperties.getOriginator(),0,(Object)text),
                    "oto"));
        }};
        return Try.of(() -> imiWebClient.sendXmsRequest(String.format(Constants.SAMPLE_SOAP_REQUEST,
                xmlMapper.writeValueAsString(map)
                        .replace("<>","")
                        .replace("</>","")))
                .doOnSuccess(response -> log.info("IMI Response : '{}'",response))
                .filter(response -> response.contains("status=\"40\""))
                .flatMap(response -> Mono.just(ResponseEntity.ok().build()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(400).build())))
                .getOrElseThrow(() -> new BaseException(messages.get(Response.INTERNAL_SERVER_ERROR)));
    }

    public Mono<String> sendPushOtpRequest(String recipient, Integer cost) {

        Map<String,Object> map = new HashMap<String,Object>() {{
            this.put("action",Action.PUSHOTP.getValue());
            this.put("password",vasProperties.getPassword());
            this.put("userid",vasProperties.getUserid());
            this.put("body",new BaseBody(vasProperties.getServiceid(),
                    new Recipient(recipient,vasProperties.getOriginator(),cost)));
        }};

        return Try.of(() -> imiWebClient.sendXmsRequest(String.format(Constants.SAMPLE_SOAP_REQUEST,
                xmlMapper.writeValueAsString(map)
                        .replace("<>","")
                        .replace("</>","")))
                .filter(res -> res.contains("status=\"40\""))
                .map(resBody -> StringUtils.substringBefore(
                        StringUtils.substringAfter(
                                StringEscapeUtils.unescapeHtml4(resBody),"status=\"40\">"
                        ),"</recipient"
                ).trim())
                .switchIfEmpty(Mono.error(new BadRequestException("invalid status from imi"))))
                .getOrElseThrow(() -> new BaseException(messages.get(Response.INTERNAL_SERVER_ERROR)));
    }

    public Try<String> sendChargeOtpRequest(String recipient, String pin, Integer otpid) {

        Map<String,Object> map = new HashMap<String,Object>() {{
            this.put("action",Action.CHARGEOTP.getValue());
            this.put("userid",vasProperties.getUserid());
            this.put("password",vasProperties.getPassword());
            this.put("body",new BaseBody(vasProperties.getServiceid(),
                    new Recipient(recipient,vasProperties.getOriginator(),(Object) otpid,pin)));
        }};

        return Try.of(() -> imiWebClient.sendXmsRequest(String.format(Constants.SAMPLE_SOAP_REQUEST,
                xmlMapper.writeValueAsString(map).replace("<>","").replace("</>","")))
                .blockOptional()
                .filter(res -> res.contains("status=\"40\"") || res.contains("status=\"41\""))
                .orElseThrow(() -> new BadRequestException("error")));
    }
}
