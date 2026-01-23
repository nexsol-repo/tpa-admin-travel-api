package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InsurancePeriod(LocalDateTime startDate, LocalDateTime endDate) {
    public boolean isValid(LocalDateTime now) {
        return now.isAfter(startDate) && now.isBefore(endDate);
    }
}
