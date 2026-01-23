package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record InsuredPerson(String name, String englishName, String residentNumber, String passportNumber,
        String gender, BigDecimal individualPremium, String iIndividualPolicyNumber) {
}
