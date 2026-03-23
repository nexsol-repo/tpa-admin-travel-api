package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelInsureRefundJpaRepository extends JpaRepository<TravelRefundEntity, Long> {

	Optional<TravelRefundEntity> findByContractId(Long contractId);

	List<TravelRefundEntity> findByContractIdIn(List<Long> contractIds);

}