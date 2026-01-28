package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelInsurerJpaRepository extends JpaRepository<TravelInsurerEntity, Long> {

    List<TravelInsurerEntity> findByIsActiveTrue();

}
