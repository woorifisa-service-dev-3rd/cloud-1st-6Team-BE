package com.lunch.backend.core.exception;

import com.lunch.backend.model.ResponseDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;


// 찾는 리소스가 서버에 존재하지 않음
@Getter
public class Exception404 extends RuntimeException {

    public Exception404(String message) {
        super(message);
    }

    public ResponseDTO<?> body() {
        return new ResponseDTO<>(HttpStatus.NOT_FOUND, "notFound", getMessage());
    }

    public HttpStatus status() {
        return HttpStatus.NOT_FOUND;
    }
}