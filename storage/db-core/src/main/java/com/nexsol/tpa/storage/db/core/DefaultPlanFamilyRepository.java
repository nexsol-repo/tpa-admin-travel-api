package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.plan.PlanFamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DefaultPlanFamilyRepository implements PlanFamilyRepository {

	private final TravelInsurancePlanFamilyJpaRepository familyJpaRepository;

	@Override
	public Optional<String> findFamilyNameByExactName(String familyName, boolean isLoss) {
		return familyJpaRepository.findByFamilyNameAndIsLoss(familyName, isLoss)
			.map(TravelInsurancePlanFamilyEntity::getFamilyName);
	}

	@Override
	public Optional<Long> findFamilyIdByExactName(String familyName, boolean isLoss) {
		return familyJpaRepository.findByFamilyNameAndIsLoss(familyName, isLoss)
			.map(TravelInsurancePlanFamilyEntity::getId);
	}

}