package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContractMeta(String policyNumber, String partnerName, String channelName, LocalDateTime applicationDate,
        InsurancePeriod period) {
}
