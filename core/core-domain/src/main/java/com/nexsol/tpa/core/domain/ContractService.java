package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractReader contractReader;

    private final ContractFinder contractFinder;

    public InsuranceContract getContractDetail(Long contractId) {
        return contractReader.read(contractId);
    }

    public PageResult<InsuranceContract> searchContract(ContractSearchCriteria criteria, SortPage sortPage) {
        return contractFinder.find(criteria, sortPage);
    }

}
