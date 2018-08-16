package com.emersun.imi;

import com.emersun.imi.configs.MongodbUtil;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class Application {
    @Autowired
    private MongodbUtil mongodbUtil;
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    @PostConstruct
    public void init() {
        mongodbUtil.insertReports();
        mongodbUtil.insertAdminUserAccount();
    }
}
