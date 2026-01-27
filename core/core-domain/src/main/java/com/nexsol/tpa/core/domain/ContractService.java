package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ServiceType;
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

    private static final ServiceType DEFAULT_SERVICE_TYPE = ServiceType.TRAVEL;

    private final ContractReader contractReader;

    private final ContractFinder contractFinder;

    private final ContractUpdater contractUpdater;

    private final ContractChangeDetector contractChangeDetector;

    private final MemoRegistrar memoRegistrar;

    private final SystemLogRegistrar systemLogRegistrar;

    @Transactional
    public InsuranceContract getContractDetail(Long contractId) {
        return contractReader.read(contractId);
    }

    @Transactional
    public PageResult<InsuranceContract> searchContract(ContractSearchCriteria criteria, SortPage sortPage) {
        return contractFinder.find(criteria, sortPage);
    }

    /**
     * 계약 수정 + 메모 등록 + 시스템 로그 등록 비즈니스 흐름: 1. 기존 계약 조회 → 2. 변경 감지 → 3. 계약 수정 → 4. 메모 등록 →
     * 5. 시스템 로그 등록 메모/시스템로그 등록 실패 시 전체 롤백
     */
    @Transactional
    public InsuranceContract updateContract(ContractUpdateCommand command) {
        // 1. 기존 계약 조회 (변경 감지용)
        InsuranceContract existing = contractReader.read(command.contractId());

        // 2. 변경 감지 (수정 전에 비교)
        String changeLog = contractChangeDetector.detectChanges(existing, command);

        // 3. 계약 수정
        InsuranceContract updated = contractUpdater.update(command);

        // 4. 메모 등록
        memoRegistrar.register(updated.contractId(), command.memo(), DEFAULT_SERVICE_TYPE);

        // 5. 시스템 로그 등록
        systemLogRegistrar.register(updated.contractId(), changeLog, DEFAULT_SERVICE_TYPE);

        return updated;
    }

}
