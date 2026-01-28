package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.plan.Plan;

public record PlanResponse(Long id, String value, String label, String fullName, Long insurerId) {

    public static PlanResponse of(Plan plan) {
        return new PlanResponse(plan.id(), plan.code(), plan.name(), plan.fullName(), plan.insurerId());
    }

}
