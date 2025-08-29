package ru.cft.dto.seller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.cft.validation.NullOrNotBlank;

@Builder
public record SellerPatchDto(
        @NotNull
        @Schema(description = "Unique seller identifier", example = "42")
        Long id,
        @NullOrNotBlank
        @Schema(description = "Seller name", example = "John Doe")
        String name,
        @NullOrNotBlank
        @Schema(description = "Contact information", example = "john@doe.com")
        String contactInfo
) {
}
