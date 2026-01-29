package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChannelJpaRepository extends JpaRepository<TravelChannelEntity, Long> {

    List<TravelChannelEntity> findByIsActiveTrue();

    @Query("SELECT c FROM TravelChannelEntity c WHERE c.partnerId = :partnerId AND c.serviceType LIKE %:serviceType% AND c.isActive = true")
    List<TravelChannelEntity> findAllByPartnerIdAndServiceType(@Param("partnerId") Long partnerId,
            @Param("serviceType") String serviceType);

}
