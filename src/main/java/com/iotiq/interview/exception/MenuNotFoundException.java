package com.iotiq.interview.exception;

import java.util.UUID;

public class MenuNotFoundException extends RuntimeException {
    public MenuNotFoundException() {
        super("Menu not found");
    }
    
    public MenuNotFoundException(String message) {
        super(message);
    }

    public MenuNotFoundException(UUID id) {
        super("Menu not found with id: " + id);
    }
}
