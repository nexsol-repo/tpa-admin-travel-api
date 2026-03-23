package com.nexsol.tpa.core.domain.product;

import lombok.Builder;

@Builder
public record ProductPlan(Long planId, Long familyId, String productName, String planName, String displayPlanName,
		boolean silsonExclude, String travelCountry, String countryCode, String coverageLink) {
}
