package com.nexsol.tpa.core.domain.product;

import lombok.Builder;

@Builder
public record ProductPlan(Long planId, String productName, String planName, String travelCountry, String countryCode,
        String coverageLink) {
}
