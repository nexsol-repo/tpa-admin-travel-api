package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuredPersonJpaRepository extends JpaRepository<TravelInsuredEntity, Long> {

	List<TravelInsuredEntity> findAllByContractIdAndDeletedAtIsNull(Long contractId);

	List<TravelInsuredEntity> findAllByContractIdInAndDeletedAtIsNull(List<Long> contractIds);

	void deleteAllByContractId(Long contractId);

}
