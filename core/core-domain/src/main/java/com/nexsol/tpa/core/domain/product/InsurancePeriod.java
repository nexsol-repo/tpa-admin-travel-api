package com.nexsol.tpa.core.domain.product;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record InsurancePeriod(LocalDate startDate, LocalDate endDate) {
	public boolean isValid(LocalDate now) {
		return now.isAfter(startDate) && now.isBefore(endDate);
	}
}
