package com.eddiapps.foodify.exception;

public class FoodEntryNotFoundException extends RuntimeException {
    public FoodEntryNotFoundException(String message) {
        super(message);
    }
}
