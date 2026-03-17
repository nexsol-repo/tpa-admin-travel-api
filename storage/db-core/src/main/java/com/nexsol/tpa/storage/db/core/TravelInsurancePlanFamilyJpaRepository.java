package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TravelInsurancePlanFamilyJpaRepository extends JpaRepository<TravelInsurancePlanFamilyEntity, Long> {

	@Query("SELECT f FROM TravelInsurancePlanFamilyEntity f WHERE f.familyName LIKE CONCAT(:planName, '%') AND f.isLoss = :isLoss AND f.isActive = true")
	Optional<TravelInsurancePlanFamilyEntity> findByPlanNameAndIsLoss(@Param("planName") String planName,
			@Param("isLoss") boolean isLoss);

}