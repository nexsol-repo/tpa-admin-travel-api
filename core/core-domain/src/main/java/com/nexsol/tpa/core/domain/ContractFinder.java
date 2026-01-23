package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContractFinder {

    private final ContractRepository contractRepository;

    public PageResult<InsuranceContract> find(ContractSearchCriteria criteria, SortPage sortPage) {
        return contractRepository.findAll(criteria, sortPage);
    }

}
