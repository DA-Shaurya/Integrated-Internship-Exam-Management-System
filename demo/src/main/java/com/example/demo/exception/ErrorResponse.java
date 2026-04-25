package com.example.demo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String message,
    Map<String, String> errors,  // only present on validation failures
    Instant timestamp
) {}
