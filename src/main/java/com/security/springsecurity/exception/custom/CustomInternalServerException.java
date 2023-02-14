package com.security.springsecurity.exception.custom;

import com.security.springsecurity.model.error.CustomError;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomInternalServerException extends BaseCustomException{

    public CustomInternalServerException(CustomError customError) {
        super(customError);
    }
    
}
