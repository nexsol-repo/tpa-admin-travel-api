package com.nexsol.tpa.storage.db.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TravelContractJpaRepository
        extends JpaRepository<TravelContractEntity, Long>, JpaSpecificationExecutor<TravelContractEntity> {

}
