package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;

import java.util.Optional;

public interface ContractRepository {

    Optional<InsuranceContract> findById(Long contractId);

    PageResult<InsuranceContract> findAll(ContractSearchCriteria criteria, SortPage sortPage);

    InsuranceContract save(InsuranceContract contract);

}
