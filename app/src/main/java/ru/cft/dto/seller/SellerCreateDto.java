package ru.cft.dto.seller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SellerCreateDto(
        @NotBlank
        @Schema(description = "Seller name", example = "John Doe")
        String name,
        @NotBlank
        @Schema(description = "Contact information", example = "john@doe.com")        String contactInfo
) {
}
