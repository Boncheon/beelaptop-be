package com.example.sever.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

    // định nghĩa mã lỗi HTTP trả về  mỗi khi exception này được ném ra trong quá trình xử lý request

    public ResourceNotFoundException(String message){

        super(message);
    }


}
