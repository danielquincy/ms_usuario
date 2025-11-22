package com.microservicio.exception;

public class EmailException extends Exception {
    public EmailException() {
        super();
    }

    public EmailException(String mensaje) {
        super(mensaje);
    }

}
