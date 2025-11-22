package com.microservicio.exception;

public class UserExistsException extends Exception {
    public UserExistsException() {
        super();
    }

    public UserExistsException(String mensaje) {
        super(mensaje);
    }

}
