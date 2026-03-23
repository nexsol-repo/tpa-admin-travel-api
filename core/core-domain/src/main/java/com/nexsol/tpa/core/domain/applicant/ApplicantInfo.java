package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

@Builder
public record ApplicantInfo(String name, String residentNumber, String phoneNumber, String email) {

	public static ApplicantInfo fromInsuredPerson(InsuredPerson person) {
		if (person == null) {
			return null;
		}
		return ApplicantInfo.builder()
			.name(person.name())
			.residentNumber(person.residentNumber())
			.phoneNumber(person.phone())
			.email(person.email())
			.build();
	}
}