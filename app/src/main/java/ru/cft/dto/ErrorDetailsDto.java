package ru.cft.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorDetailsDto(
        @Schema(description = "Time when the error occurred", example = "2025-08-27T12:34:56")
        LocalDateTime timestamp,
        @Schema(description = "Human-readable error message", example = "There is no seller with id 42")
        String message,
        @Schema(description = "Detailed description of the error", example = "uri=/api/v1/seller/42")
        String description,
        @Schema(description = "HTTP status code of the error (duplicates the response status)", example = "404")
        int errorCode
) {
}
