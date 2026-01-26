package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record ApplicantInfo(String name, String residentNumber, // 마스킹된 주민번호
        String phoneNumber, String email) {
    public static ApplicantInfo toApplicantInfo(Applicant applicant) {
        return ApplicantInfo.builder()
            .name(applicant.name())
            .residentNumber(masking(applicant.residentNumber())) // [중요] 변환 시 마스킹 수행
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