package com.emersun.imi.panel.controller;

import com.emersun.imi.exceptions.BaseException;
import com.emersun.imi.panel.service.LogoService;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("${api.base.url}" + "/logo")
public class LogoController {
    private final static Logger logger = LoggerFactory.getLogger(LogoController.class);
    @Autowired
    private LogoService logoService;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadLogo(HttpServletRequest request) {
        Resource resource = logoService.loadLogoAsResource();
        return Try.of(() -> request.getServletContext().getMimeType(resource.getFile().getAbsolutePath()))
                .recover(throwable -> "application/octet-stream")
                .map(contentType ->
                        ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource))
                .getOrElseThrow(() -> new BaseException("error in retrieving logo"));
    }
}
