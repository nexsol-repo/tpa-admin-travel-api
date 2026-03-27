package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;

import java.util.List;
import java.util.Optional;

public interface ContractQueryRepository {

	Optional<InsuranceContract> findById(Long contractId);

	PageResult<InsuranceContract> findAll(ContractSearchCriteria criteria, SortPage sortPage);

	List<InsuranceContract> findAll(ContractSearchCriteria criteria);

}