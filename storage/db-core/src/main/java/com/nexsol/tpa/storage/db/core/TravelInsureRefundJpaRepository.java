package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelInsureRefundJpaRepository extends JpaRepository<TravelInsureRefundEntity, Long> {

	Optional<TravelInsureRefundEntity> findByContractId(Long contractId);

	List<TravelInsureRefundEntity> findByContractIdIn(List<Long> contractIds);

}