package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 계약 서비스 (Business Layer - Coordinator) 비즈니스 흐름을 중계하고, 상세 구현은 도구 클래스에 위임
 */
@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractReader contractReader;

    private final ContractFinder contractFinder;

    private final ContractUpdater contractUpdater;

    private final MemoRegistrar memoRegistrar;

    @Transactional
    public InsuranceContract getContractDetail(Long contractId) {
        return contractReader.read(contractId);
    }

    @Transactional
    public PageResult<InsuranceContract> searchContract(ContractSearchCriteria criteria, SortPage sortPage) {
        return contractFinder.find(criteria, sortPage);
    }

    /**
     * 계약 수정 + 메모 등록 비즈니스 흐름: 1. 계약 수정 → 2. 메모 등록 메모 등록 실패 시 전체 롤백
     */
    @Transactional
    public InsuranceContract updateContract(ContractUpdateCommand command) {
        // 1. 계약 수정
        InsuranceContract updated = contractUpdater.update(command);

        // 2. 메모 등록
        memoRegistrar.register(updated.contractId(), command.memo());

        return updated;
    }

}
