package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ContractPeriod(LocalDate startDate, LocalDate endDate) {
}