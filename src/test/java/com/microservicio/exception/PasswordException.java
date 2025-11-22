package com.microservicio.exception;

public class PasswordException extends Exception{
    public PasswordException() {
        super();
    }

    public PasswordException(String mensaje) {
        super(mensaje);
    }

}
