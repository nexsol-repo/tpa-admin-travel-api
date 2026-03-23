package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TravelInsurancePlanFamilyJpaRepository extends JpaRepository<TravelInsurancePlanFamilyEntity, Long> {

	@Query("SELECT f FROM TravelInsurancePlanFamilyEntity f WHERE f.familyName = :familyName AND f.isLoss = :isLoss AND f.isActive = true")
	Optional<TravelInsurancePlanFamilyEntity> findByFamilyNameAndIsLoss(@Param("familyName") String familyName,
			@Param("isLoss") boolean isLoss);

	@Query("SELECT f FROM TravelInsurancePlanFamilyEntity f JOIN TravelInsurancePlanEntity p ON p.familyId = f.id WHERE p.id = :planId")
	Optional<TravelInsurancePlanFamilyEntity> findByPlanId(@Param("planId") Long planId);

	@Query("SELECT DISTINCT f FROM TravelInsurancePlanFamilyEntity f JOIN TravelInsurancePlanEntity p ON p.familyId = f.id WHERE p.id IN :planIds")
	List<TravelInsurancePlanFamilyEntity> findByPlanIdIn(@Param("planIds") List<Long> planIds);

}