package com.example.idempotentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationResponse {

    private boolean success;
    private String message;
    private Object data;
    private Instant timestamp;

    public static OperationResponse success(String message, Object data) {
        return OperationResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static OperationResponse error(String message) {
        return OperationResponse.builder()
                .success(false)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
}
