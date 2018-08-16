package com.emersun.imi.panel.service;

import com.emersun.imi.exceptions.BaseException;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Service
public class LogoService {
    @Value("${application.logo.path}")
    private String logoPath;

    public Resource loadLogoAsResource() {
        return Try.of(() -> new UrlResource(ResourceUtils.toURI("classpath:" + logoPath)))
                .filter(Resource::exists)
                .getOrElseThrow(() -> new BaseException("File not found " + logoPath));
    }
}
