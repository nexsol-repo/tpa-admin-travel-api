package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelJpaRepository extends JpaRepository<TravelChannelEntity, Long> {

    List<TravelChannelEntity> findByIsActiveTrue();

    @Query(value = "SELECT * FROM tpa_channel " + "WHERE partner_id = :partnerId "
            + "AND JSON_CONTAINS(service_type, JSON_QUOTE(:serviceType)) " + "AND is_active = 1", nativeQuery = true)
    List<TravelChannelEntity> findAllByPartnerIdAndServiceType(@Param("partnerId") Long partnerId,
            @Param("serviceType") String serviceType);

}