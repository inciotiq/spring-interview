package com.iotiq.interview.exception;

public class DuplicateMenuNameException extends RuntimeException {
    public DuplicateMenuNameException() {
        super("The menu already exists");
    }

    public DuplicateMenuNameException(String message) {
        super(message);
    }
}
