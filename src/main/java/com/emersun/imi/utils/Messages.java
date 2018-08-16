package com.emersun.imi.utils;

import com.emersun.imi.utils.messages.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Messages {

    @Autowired
    private MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource);
    }

    public String get(String code) {
        return accessor.getMessage(code);
    }

    public String get(Response response) {
        return accessor.getMessage(response.getMessage());
    }

    public String get(String code, Object[] params) {
        return accessor.getMessage(code, params);
    }

}