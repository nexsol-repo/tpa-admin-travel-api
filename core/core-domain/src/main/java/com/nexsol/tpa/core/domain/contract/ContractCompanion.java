package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ContractCompanion(String residentNumber, String gender, String name, String englishName,
		String englishLastName, String passportNumber, BigDecimal premium) {
}