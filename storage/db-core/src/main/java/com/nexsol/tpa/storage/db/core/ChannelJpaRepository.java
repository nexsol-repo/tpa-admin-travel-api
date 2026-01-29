package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelJpaRepository extends JpaRepository<TravelChannelEntity, Long> {

    List<TravelChannelEntity> findByIsActiveTrue();

    List<TravelChannelEntity> findByPartnerIdAndIsActiveTrue(Long partnerId);

}
