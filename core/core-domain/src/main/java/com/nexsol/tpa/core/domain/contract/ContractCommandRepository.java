package com.nexsol.tpa.core.domain.contract;

public interface ContractCommandRepository {

	Long create(InsuranceContract contract);

	Long save(InsuranceContract contract);

}