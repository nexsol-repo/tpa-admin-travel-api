package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InsuredPerson(Long id, Long planId, String planName, Boolean isContractor, String name,
		String englishName, String residentNumber, String passportNumber, String gender, String phone, String email,
		BigDecimal individualPremium, String individualPolicyNumber) {
}
