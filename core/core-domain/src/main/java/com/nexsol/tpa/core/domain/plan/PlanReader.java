package com.nexsol.tpa.core.domain.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 플랜 조회 도구 클래스 (Implement Layer)
 */
@Component
@RequiredArgsConstructor
public class PlanReader {

	private final PlanRepository planRepository;

	public Optional<Plan> read(Long id) {
		return planRepository.findById(id);
	}

	public List<Plan> readAllActive() {
		return planRepository.findAllActive();
	}

	public List<Plan> readAllActiveByInsurerId(Long insurerId) {
		return planRepository.findAllActiveByInsurerId(insurerId);
	}

	public Optional<Plan> readByPlanNamePrefixAndAgeGroupId(String planNamePrefix, Long ageGroupId) {
		return planRepository.findByPlanNamePrefixAndAgeGroupId(planNamePrefix, ageGroupId);
	}

}
