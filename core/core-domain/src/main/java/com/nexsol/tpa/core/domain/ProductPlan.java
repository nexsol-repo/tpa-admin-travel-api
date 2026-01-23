package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record ProductPlan(String productName, String planName, String travelCountry, String coverageLink) {
}
