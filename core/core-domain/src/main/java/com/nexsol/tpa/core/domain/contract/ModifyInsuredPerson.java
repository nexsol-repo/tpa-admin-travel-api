package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ModifyInsuredPerson(Long id, String name, String englishName, String residentNumber,
		String passportNumber, String gender, BigDecimal premium) {
}