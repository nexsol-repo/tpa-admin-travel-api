package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.admin.MemoRegistrar;
import com.nexsol.tpa.core.domain.admin.SystemLogRegistrar;
import com.nexsol.tpa.core.domain.applicant.InsuredPeopleUpdater;
import com.nexsol.tpa.core.enums.ServiceType;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 계약 서비스 (Business Layer - Coordinator) 비즈니스 흐름을 중계하고, 상세 구현은 도구 클래스에 위임
 */
@Service
@RequiredArgsConstructor
public class ContractService {

	private static final ServiceType DEFAULT_SERVICE_TYPE = ServiceType.TRAVEL;

	private final ContractReader contractReader;

	private final ContractFinder contractFinder;

	private final ContractCreator contractCreator;

	private final ContractUpdater contractUpdater;

	private final ContractWriter contractWriter;

	private final ContractChangeDetector contractChangeDetector;

	private final InsuredPeopleUpdater insuredPeopleUpdater;

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
	 * 계약 직접 등록 + 메모 등록 + 시스템 로그 등록 비즈니스 흐름: 1. 계약 생성 → 2. 메모 등록 → 3. 시스템 로그 등록 메모/시스템로그
	 * 등록 실패 시 전체 롤백
	 */
	@Transactional
	public Long createContract(NewContract newContract) {
		// 1. 도메인 객체 생성
		InsuranceContract contract = contractCreator.create(newContract);

		// 2. DB 저장
		Long contractId = contractWriter.write(contract);

		// 3. 메모 등록
		memoRegistrar.register(contractId, newContract.memo(), DEFAULT_SERVICE_TYPE);

		// 4. 시스템 로그 등록
		systemLogRegistrar.register(contractId, "계약 직접 등록", DEFAULT_SERVICE_TYPE);

		return contractId;
	}

	@Transactional
	public Long updateContract(ModifyContract modifyContract) {
		// 1. 기존 계약 조회
		InsuranceContract existing = contractReader.read(modifyContract.contractId());

		// 2. 변경 감지 (수정 전에 비교)
		String changeLog = contractChangeDetector.detectChanges(existing, modifyContract);

		// 3. 도메인 객체 수정
		InsuranceContract updated = contractUpdater.update(existing, modifyContract);

		// 4. DB 저장
		Long contractId = contractWriter.write(updated);

		// 5. 피보험자 수정
		insuredPeopleUpdater.update(contractId, updated.insuredPeople());

		// 6. 메모 등록
		memoRegistrar.register(contractId, modifyContract.memo(), DEFAULT_SERVICE_TYPE);

		// 7. 시스템 로그 등록
		systemLogRegistrar.register(contractId, changeLog, DEFAULT_SERVICE_TYPE);

		return contractId;
	}

	public List<InsuranceContract> downloadContracts(ContractSearchCriteria criteria) {
		// 1. 기간 설정 여부 검증 (필수 요구사항)
		if (criteria.startDate() == null || criteria.endDate() == null) {
			throw new CoreException(CoreErrorType.INVALID_REQUEST, "엑셀 다운로드 시 조회 기간(시작일, 종료일)은 필수입니다.");
		}

		// 2. 전체 데이터 조회 (Repository의 findAll 리스트 버전 호출)
		return contractFinder.findList(criteria);
	}

}
