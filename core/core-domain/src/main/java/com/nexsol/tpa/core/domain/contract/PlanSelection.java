package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.enums.PlanGrade;
import lombok.Builder;

@Builder
public record PlanSelection(Long planId, String planName, Boolean silsonExclude, PlanGrade planGrade,
		String travelCountry, String countryCode) {
}