package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

@Builder
public record ApplicantInfo(String name, String residentNumber, // 마스킹된 주민번호
        String phoneNumber, String email) {
    public static ApplicantInfo toApplicantInfo(Applicant applicant) {
        return ApplicantInfo.builder()
            .name(applicant.name())
            .residentNumber(applicant.residentNumber())
            .phoneNumber(applicant.phoneNumber())
            .email(applicant.email())
            .build();
    }

    private static String masking(String origin) {
        if (origin == null || origin.length() < 8)
            return origin;
        return origin.substring(0, 7) + "*******";
    }
}