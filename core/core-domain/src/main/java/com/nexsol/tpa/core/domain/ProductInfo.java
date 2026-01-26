package com.nexsol.tpa.core.domain;

public record ProductInfo(String name, String plan, String country) {
    public static ProductInfo toProductInfo(ProductPlan plan) {
        return new ProductInfo(plan.productName(), plan.planName(), plan.travelCountry());
    }
}