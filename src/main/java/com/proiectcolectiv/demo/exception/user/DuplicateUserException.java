package com.proiectcolectiv.demo.exception.user;

public class DuplicateUserException extends Exception {
    public DuplicateUserException() {
        super("User with the given email already exists.");
    }
}
