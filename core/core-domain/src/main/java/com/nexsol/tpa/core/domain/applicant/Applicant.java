package com.nexsol.tpa.core.domain.applicant;

import lombok.Builder;

@Builder
public record Applicant(String name, String residentNumber, String phoneNumber, String email) {
}
