package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartnerJpaRepository extends JpaRepository<TravelPartnerEntity, Long> {

	List<TravelPartnerEntity> findByIsActiveTrue();

	@Query(value = "SELECT * FROM tpa_partner " + "WHERE JSON_CONTAINS(service_type, JSON_QUOTE(:serviceType)) "
			+ "AND is_active = 1", nativeQuery = true)
	List<TravelPartnerEntity> findAllActiveByServiceType(@Param("serviceType") String serviceType);

}
