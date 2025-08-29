package ru.cft.dto.seller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SellerUpdateDto(
        @NotNull
        @Schema(description = "Unique seller identifier", example = "42")
        Long id,
        @NotBlank
        @Schema(description = "Seller name", example = "John Doe")
        String name,
        @NotBlank
        @Schema(description = "Contact information", example = "john@doe.com")
        String contactInfo
) {
}
