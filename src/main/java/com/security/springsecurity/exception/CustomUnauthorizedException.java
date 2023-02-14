package com.security.springsecurity.exception;

import com.security.springsecurity.exception.custom.BaseCustomException;
import com.security.springsecurity.model.error.CustomError;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CustomUnauthorizedException  extends BaseCustomException{

    public CustomUnauthorizedException(CustomError customError) {
        super(customError);
    }
    
}
