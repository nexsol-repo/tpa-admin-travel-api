package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ApplicantInfo(String name, String englishName, String residentNumber, String phoneNumber, String email,
		BigDecimal premium) {

	public static ApplicantInfo fromInsuredPerson(InsuredPerson person) {
		if (person == null) {
			return null;
		}
		return ApplicantInfo.builder()
			.name(person.name())
			.englishName(person.englishName())
			.residentNumber(person.residentNumber())
			.phoneNumber(person.phone())
			.email(person.email())
			.premium(person.individualPremium())
			.build();
	}
}