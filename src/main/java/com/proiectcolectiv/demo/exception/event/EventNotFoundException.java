package com.proiectcolectiv.demo.exception.event;

public class EventNotFoundException extends Exception {
    public EventNotFoundException() {
        super("No Event found.");
    }
}
