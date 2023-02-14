package com.security.springsecurity.exception.custom;

import java.util.HashMap;
import java.util.Map;

import com.security.springsecurity.model.error.CustomError;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public abstract class BaseCustomException extends Exception{
    protected Map<String,CustomError> errorHashMap;
    public BaseCustomException(CustomError customError) {
        errorHashMap = new HashMap<>();
        errorHashMap.put("error", customError);
    }
}
