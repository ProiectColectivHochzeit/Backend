package com.proiectcolectiv.demo.exception.auth;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException() {
        super("Invalid password provided.");
    }
}
