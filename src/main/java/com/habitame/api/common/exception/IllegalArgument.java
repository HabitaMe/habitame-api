package com.habitame.api.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalArgument extends RuntimeException {
    public IllegalArgument(String msg) {
        super(msg);
    }

}
