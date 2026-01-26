package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContractMeta(String policyNumber, SubscriptionOrigin origin, LocalDateTime applicationDate,
        InsurancePeriod period) {
}
