package com.nexsol.tpa.core.domain.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 플랜 조회 도구 클래스 (Implement Layer)
 */
@Component
@RequiredArgsConstructor
public class PlanReader {

    private final PlanRepository planRepository;

    public List<Plan> readAllActive() {
        return planRepository.findAllActive();
    }

    public List<Plan> readAllActiveByInsurerId(Long insurerId) {
        return planRepository.findAllActiveByInsurerId(insurerId);
    }

}
