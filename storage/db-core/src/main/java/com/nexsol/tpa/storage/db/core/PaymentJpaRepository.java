package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<TravelInsurePaymentEntity, Long> {

	Optional<TravelInsurePaymentEntity> findByContractId(Long contractId);

	List<TravelInsurePaymentEntity> findByContractIdIn(List<Long> contractIds);

}
