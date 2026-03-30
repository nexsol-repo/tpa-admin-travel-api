package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CompanionInfo(Long id, Long planId, String planName, Boolean isContractor, String name,
		String englishName, String residentNumber, String passportNumber, String gender, String phone, String email,
		BigDecimal premium) {
	public static CompanionInfo of(InsuredPerson person) {
		return CompanionInfo.builder()
			.id(person.id())
			.planId(person.planId())
			.planName(person.planName())
			.isContractor(person.isContractor())
			.name(person.name())
			.englishName(person.englishName())
			.residentNumber(person.residentNumber())
			.passportNumber(person.passportNumber())
			.gender(person.gender())
			.phone(person.phone())
			.email(person.email())
			.premium(person.individualPremium())
			.build();
	}

	private static String masking(String origin) {
		if (origin == null || origin.length() < 8)
			return origin;
		return origin.substring(0, 7) + "*******";
	}

}