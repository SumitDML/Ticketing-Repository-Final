package com.ticketing.api.exception;

public class MailException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public MailException(final String message) {
        super(message);
    }
}
