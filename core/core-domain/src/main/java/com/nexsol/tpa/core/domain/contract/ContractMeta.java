package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ContractMeta(String policyNumber, SubscriptionOrigin origin, LocalDateTime applicationDate,
        InsurancePeriod period) {
}
