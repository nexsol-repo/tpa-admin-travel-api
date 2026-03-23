package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<TravelPaymentEntity, Long> {

	Optional<TravelPaymentEntity> findByContractId(Long contractId);

	List<TravelPaymentEntity> findByContractIdIn(List<Long> contractIds);

}
