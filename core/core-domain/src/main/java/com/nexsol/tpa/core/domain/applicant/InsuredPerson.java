package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InsuredPerson(String name, String englishName, String residentNumber, String passportNumber,
        String gender, BigDecimal individualPremium, String individualPolicyNumber) {
}
