package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.plan.PlanFamilyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlanFamilyReaderImpl implements PlanFamilyReader {

	private final TravelInsurancePlanFamilyJpaRepository familyJpaRepository;

	@Override
	public Optional<String> findFamilyName(String planName, boolean isLoss) {
		return familyJpaRepository.findByPlanNameAndIsLoss(planName, isLoss)
			.map(TravelInsurancePlanFamilyEntity::getFamilyName);
	}

}