package com.security.springsecurity.exception.custom;

import com.security.springsecurity.model.error.CustomError;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomDuplicateFieldException  extends BaseCustomException{

    public CustomDuplicateFieldException(CustomError customError) {
        super(customError);
    }
    
}
