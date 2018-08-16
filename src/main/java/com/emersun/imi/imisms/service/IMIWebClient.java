package com.emersun.imi.imisms.service;

import com.emersun.imi.exceptions.BadRequestException;
import com.emersun.imi.exceptions.BaseException;
import com.emersun.imi.utils.Messages;
import com.emersun.imi.utils.messages.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import javax.annotation.PostConstruct;
import java.util.logging.Level;

@Service
public class IMIWebClient {
    private final static Logger log = LoggerFactory.getLogger(IMIWebClient.class);
    private WebClient webClient;
    @Value("${mci.url}")
    private String webServiceUrl;
    @Autowired
    private Messages messages;

    @PostConstruct
    private void initialize() {
        webClient = WebClient.builder()
                .baseUrl(webServiceUrl)
                .defaultHeader("Content-Type", "text/xml; charset=utf-8")
                .defaultHeader("SOAPAction","http://tempuri.org/XmsRequest")
                .filter(logRequest())
                .build();
    }

    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }


    public Mono<String> sendXmsRequest(String xmsRequest) {
        log.info("SOAP Request Body : {}",xmsRequest);
        return webClient.post()
                .body(Mono.just(xmsRequest),
                        String.class)
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new BaseException(messages.get(Response.INTERNAL_SERVER_ERROR))))
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(new BadRequestException(messages.get(Response.MESSAGE_ERROR))))
                .bodyToMono(String.class);
    }
}
