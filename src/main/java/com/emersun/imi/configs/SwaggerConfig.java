package com.emersun.imi.configs;

import com.google.common.base.Predicates;
import org.assertj.core.util.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emersun.imi.panel.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Lists.newArrayList(apiKey()));
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("IMI ADMIN PANEL REST API")
                .contact(new Contact("Farzam Vatanzadeh","","farzam.vat@gmail.com"))
                .build();
    }

    @Bean SecurityConfiguration security() {
        return new SecurityConfiguration(null, null, null, null,
                "Bearer access_token", ApiKeyVehicle.HEADER,
                "Authorization", ",");
    }

    private ApiKey apiKey() {
        return new ApiKey("X-AUTH-TOKEN", "X-AUTH-TOKEN", "header");
    }
}
