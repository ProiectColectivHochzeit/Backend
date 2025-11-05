package com.proiectcolectiv.demo.exception.auth;

public class InvalidTokenException extends Exception {
    public InvalidTokenException() {
        super("Invalid token provided.");
    }
}
