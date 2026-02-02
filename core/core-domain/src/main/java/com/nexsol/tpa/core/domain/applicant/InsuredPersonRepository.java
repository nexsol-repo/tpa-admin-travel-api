package com.nexsol.tpa.core.domain.applicant;

import java.util.List;

/**
 * 피보험자(동반자) Repository 인터페이스 - Implement Layer에서 정의, Data Access Layer에서 구현
 */
public interface InsuredPersonRepository {

	List<InsuredPerson> findAllByContractId(Long contractId);

	void softDeleteByIds(List<Long> ids);

	void updateAll(List<InsuredPerson> people);

	void createAll(Long contractId, List<InsuredPerson> people);

}
