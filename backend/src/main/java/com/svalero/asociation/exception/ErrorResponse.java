package com.svalero.asociation.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private String message;
    private String title;
    private Map<String, String> error = new HashMap<>();


    public static ErrorResponse generalError(int code, String message, String title){
        return new ErrorResponse(code, message, title, new HashMap<>());
    }

    public void addError (String field, String message){
        this.error.put(field, message);
    }
}
