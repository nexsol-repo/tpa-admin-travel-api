package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CompanionInfo(String name, String englishName, String residentNumber, String passportNumber,
        String gender, BigDecimal premium, String policyNumber) {
    public static CompanionInfo of(InsuredPerson person) {
        return CompanionInfo.builder()
            .name(person.name())
            .englishName(person.englishName())
            .residentNumber(person.residentNumber())
            .passportNumber(person.passportNumber())
            .gender(person.gender())
            .premium(person.individualPremium())
            .policyNumber(person.individualPolicyNumber())
            .build();
    }

    private static String masking(String origin) {
        if (origin == null || origin.length() < 8)
            return origin;
        return origin.substring(0, 7) + "*******";
    }

}