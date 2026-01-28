package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.plan.Plan;
import com.nexsol.tpa.core.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlanRepositoryImpl implements PlanRepository {

    private final TravelInsurancePlanJpaRepository planJpaRepository;

    @Override
    public List<Plan> findAllActive() {
        return planJpaRepository.findByIsActiveTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Plan> findAllActiveByInsurerId(Long insurerId) {
        return planJpaRepository.findByInsurerIdAndIsActiveTrue(insurerId).stream().map(this::toDomain).toList();
    }

    private Plan toDomain(TravelInsurancePlanEntity entity) {
        return new Plan(entity.getId(), entity.getPlanCode(), entity.getPlanName(), entity.getPlanFullName(),
                entity.getInsurerId());
    }

}
