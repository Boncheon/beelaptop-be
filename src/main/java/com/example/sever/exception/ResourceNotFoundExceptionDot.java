package com.example.sever.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)

public class ResourceNotFoundExceptionDot extends RuntimeException{


    public ResourceNotFoundExceptionDot(String message){

        super(message);
    }

    

}
