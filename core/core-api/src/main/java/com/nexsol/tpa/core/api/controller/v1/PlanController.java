package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.PlanResponse;
import com.nexsol.tpa.core.domain.plan.PlanService;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class PlanController {

    private final PlanService planService;

    @GetMapping("/plan")
    public ApiResponse<List<PlanResponse>> getPlans(
            @RequestParam(required = false) Long insurerId) {

        List<PlanResponse> plans;

        if (insurerId != null) {
            plans = planService.getActivePlansByInsurerId(insurerId)
                    .stream()
                    .map(PlanResponse::of)
                    .toList();
        } else {
            plans = planService.getActivePlans()
                    .stream()
                    .map(PlanResponse::of)
                    .toList();
        }

        return ApiResponse.success(plans);
    }

}