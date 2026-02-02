package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuredPersonJpaRepository extends JpaRepository<TravelInsurePeopleEntity, Long> {

	List<TravelInsurePeopleEntity> findAllByContractIdAndDeletedAtIsNull(Long contractId);

	List<TravelInsurePeopleEntity> findAllByContractIdInAndDeletedAtIsNull(List<Long> contractIds);

	void deleteAllByContractId(Long contractId);

}
