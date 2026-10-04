package com.mercury.javadeploypipeline.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDetail> details
) {
    public record FieldErrorDetail(
            String field,
            Object rejectedValue,
            String message
    ) {
    }

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, null);
    }

    public static ErrorResponse withDetails(int status, String error, String message, String path, List<FieldErrorDetail> details) {
        return new ErrorResponse(Instant.now(), status, error, message, path, details);
    }
}
