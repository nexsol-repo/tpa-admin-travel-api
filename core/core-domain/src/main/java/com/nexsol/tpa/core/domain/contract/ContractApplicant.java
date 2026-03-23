package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

@Builder
public record ContractApplicant(String name, String residentNumber, String phoneNumber, String email) {
}