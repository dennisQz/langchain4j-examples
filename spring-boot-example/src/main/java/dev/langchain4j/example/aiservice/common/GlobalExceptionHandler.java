package dev.langchain4j.example.aiservice.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<Map<String, String>> handleException(Exception e) {
        Map<String, String> data = new HashMap<>();
        data.put("message", e.getMessage() != null ? e.getMessage() : "Internal Server Error");
        return ApiResponse.error(500, data);
    }
}
