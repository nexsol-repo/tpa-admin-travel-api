package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelInsurancePlanJpaRepository extends JpaRepository<TravelInsurancePlanEntity, Long> {

    List<TravelInsurancePlanEntity> findByIdIn(List<Long> ids);

    List<TravelInsurancePlanEntity> findByIsActiveTrue();

    List<TravelInsurancePlanEntity> findByInsurerIdAndIsActiveTrue(Long insurerId);

}