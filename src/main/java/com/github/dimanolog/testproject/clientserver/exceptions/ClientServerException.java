package com.github.dimanolog.testproject.clientserver.exceptions;

public class ClientServerException extends RuntimeException {
    public ClientServerException(String message) {
        super(message);
    }

    public ClientServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
