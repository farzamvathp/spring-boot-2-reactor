package com.emersun.imi.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class JacksonConfig implements WebMvcConfigurer {

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON_UTF8)
        .ignoreAcceptHeader(true)
        .favorPathExtension(true)
        .mediaType("json",MediaType.APPLICATION_JSON_UTF8)
        .mediaType("xml",MediaType.APPLICATION_XML);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
        .serializationInclusion(JsonInclude.Include.NON_NULL)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .modules(new JavaTimeModule())
        .build();
    }
}