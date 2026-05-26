package com.svalero.asociation.exception;

public  class TrabajadorNotFoundException extends ResourceNotFoundException {
    public TrabajadorNotFoundException(String message) {
        super(message);
    }
}