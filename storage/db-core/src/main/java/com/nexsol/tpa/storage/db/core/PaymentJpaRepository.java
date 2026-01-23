package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<TravelInsurePaymentEntity, Long> {

    Optional<TravelInsurePaymentEntity> findByContractId(Long contractId);

}
