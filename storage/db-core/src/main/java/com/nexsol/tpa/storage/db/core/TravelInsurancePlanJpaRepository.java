package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TravelInsurancePlanJpaRepository extends JpaRepository<TravelInsurancePlanEntity, Long> {

	List<TravelInsurancePlanEntity> findByIdIn(List<Long> ids);

	List<TravelInsurancePlanEntity> findByIsActiveTrue();

	List<TravelInsurancePlanEntity> findByInsurerIdAndIsActiveTrue(Long insurerId);

	@Query("SELECT p FROM TravelInsurancePlanEntity p WHERE p.planFullName LIKE CONCAT(:planNamePrefix, '%') AND p.ageGroupId = :ageGroupId AND p.isActive = true")
	Optional<TravelInsurancePlanEntity> findByPlanNamePrefixAndAgeGroupId(
			@Param("planNamePrefix") String planNamePrefix, @Param("ageGroupId") Long ageGroupId);

	@Query("SELECT p FROM TravelInsurancePlanEntity p WHERE p.familyId = :familyId AND p.ageGroupId = :ageGroupId AND p.isActive = true")
	Optional<TravelInsurancePlanEntity> findByFamilyIdAndAgeGroupId(@Param("familyId") Long familyId,
			@Param("ageGroupId") Long ageGroupId);

}