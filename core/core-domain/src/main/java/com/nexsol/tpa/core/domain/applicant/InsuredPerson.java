package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InsuredPerson(Long id, String name, String englishName, String residentNumber, String passportNumber,
		String gender, BigDecimal individualPremium, String individualPolicyNumber) {
}
