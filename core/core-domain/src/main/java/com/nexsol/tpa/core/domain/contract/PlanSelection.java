package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

@Builder
public record PlanSelection(Long planId, String planName, Boolean silsonExclude, String travelCountry,
		String countryCode) {
}