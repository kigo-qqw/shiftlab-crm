package ru.cft.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DateRangeDto(
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
