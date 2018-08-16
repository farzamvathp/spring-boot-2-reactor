package com.emersun.imi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(message);
    }
}
