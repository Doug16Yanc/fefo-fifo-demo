package tech.fefofifodemo.exception.global;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        String error,
        @JsonFormat(pattern = "yyyy-MM-DD HH:mm:ss")
        LocalDateTime timestamp
) {
    public ErrorResponse(String message, HttpStatus httpStatus) {
        this(message, httpStatus.value(), httpStatus.getReasonPhrase(), LocalDateTime.now());
    }
}