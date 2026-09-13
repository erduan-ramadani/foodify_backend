package com.eddiapps.foodify.dto;

import java.time.Instant;

public class ApiErrorResponse {
    private int status;
    private String message;
    private Instant timestamp;

    public ApiErrorResponse(int status, String message, Instant timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
