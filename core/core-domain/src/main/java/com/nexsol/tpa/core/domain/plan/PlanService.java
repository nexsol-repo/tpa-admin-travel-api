package com.nexsol.tpa.core.domain.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 플랜 서비스 (Business Layer)
 * - 비즈니스 흐름을 중계하는 Coordinator 역할
 */
@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanReader planReader;

    public List<Plan> getActivePlans() {
        return planReader.readAllActive();
    }

    public List<Plan> getActivePlansByInsurerId(Long insurerId) {
        return planReader.readAllActiveByInsurerId(insurerId);
    }

}