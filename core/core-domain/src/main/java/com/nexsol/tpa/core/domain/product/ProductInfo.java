package com.nexsol.tpa.core.domain.product;

public record ProductInfo(String name, String plan, String country, String countryCode) {
	public static ProductInfo toProductInfo(ProductPlan plan) {
		return new ProductInfo(plan.productName(), plan.planName(), plan.travelCountry(), plan.countryCode());
	}
}