package com.nexsol.tpa.core.domain.product;

public record ProductInfo(String name, String plan, String displayPlanName, boolean silsonExclude, String country,
		String countryCode) {
	public static ProductInfo toProductInfo(ProductPlan plan) {
		return new ProductInfo(plan.productName(), plan.planName(), plan.displayPlanName(), plan.silsonExclude(),
				plan.travelCountry(), plan.countryCode());
	}
}