package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.contract.ContractRepository;
import com.nexsol.tpa.core.domain.contract.ContractSearchCriteria;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.payment.RefundInfo;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepository {

	private static final class Fields {

		static final String ID = "id";
		static final String APPLY_DATE = "applyDate";
		static final String INSURE_START_DATE = "insureStartDate";
		static final String INSURE_END_DATE = "insureEndDate";
		static final String PARTNER_NAME = "partnerName";
		static final String CHANNEL_NAME = "channelName";
		static final String INSURER_NAME = "insurerName";
		static final String STATUS = "status";
		static final String APPLICANT_NAME = "applicantName";
		static final String INSURED_PEOPLE_NUMBER = "insuredPeopleNumber";
		static final String DELETED_AT = "deletedAt";

	}

	private final TravelContractJpaRepository travelContractJpaRepository;

	private final PaymentJpaRepository paymentJpaRepository;

	private final InsuredPersonJpaRepository insuredPersonJpaRepository;

	private final TravelInsurancePlanJpaRepository planJpaRepository;

	private final TravelInsureRefundJpaRepository refundJpaRepository;

	private final TravelInsurancePlanFamilyJpaRepository familyJpaRepository;

	private final TravelInsurancePlanFamilyMapJpaRepository familyMapJpaRepository;

	private final TravelContractMapper travelContractMapper;

	@Override
	public Optional<InsuranceContract> findById(Long contractId) {
		var contractEntityOptional = travelContractJpaRepository.findById(contractId);

		if (contractEntityOptional.isEmpty()) {
			return Optional.empty();
		}

		TravelContractEntity contract = contractEntityOptional.get();

		var payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
		var refund = refundJpaRepository.findByContractId(contractId).orElse(null);
		var people = insuredPersonJpaRepository.findAllByContractIdAndDeletedAtIsNull(contractId);
		var plan = fetchPlan(contract.getPlanId());
		var family = (contract.getPlanId() != null)
				? familyJpaRepository.findByPlanId(contract.getPlanId()).orElse(null) : null;

		return Optional.of(travelContractMapper.toDomain(contract, payment, refund, people, plan, family));
	}

	private TravelInsurancePlanEntity fetchPlan(Long planId) {
		if (planId == null) {
			return null;
		}
		return planJpaRepository.findById(planId).orElse(null);
	}

	@Override
	public Long create(InsuranceContract contract) {
		// 1. 계약 엔티티 생성 및 저장
		TravelContractEntity contractEntity = travelContractMapper.toEntity(contract);
		TravelContractEntity savedContract = travelContractJpaRepository.save(contractEntity);
		Long contractId = savedContract.getId();

		// 2. 피보험자(동반자) 엔티티 생성 및 저장
		if (contract.insuredPeople() != null && !contract.insuredPeople().isEmpty()) {
			List<TravelInsurePeopleEntity> peopleEntities = contract.insuredPeople()
				.stream()
				.map(person -> TravelInsurePeopleEntity.create(contractId, person))
				.toList();
			insuredPersonJpaRepository.saveAll(peopleEntities);
		}

		// 3. 결제 정보 엔티티 생성 및 저장
		if (contract.paymentInfo() != null) {
			TravelInsurePaymentEntity paymentEntity = TravelInsurePaymentEntity.create(contractId,
					contract.paymentInfo());
			TravelInsurePaymentEntity savedPayment = paymentJpaRepository.save(paymentEntity);

			// 4. 환불 정보 엔티티 생성 및 저장
			if (contract.refundInfo() != null) {
				TravelInsureRefundEntity refundEntity = TravelInsureRefundEntity.create(savedPayment.getId(),
						contractId, contract.refundInfo());
				refundJpaRepository.save(refundEntity);
			}
		}

		return contractId;
	}

	@Override
	public PageResult<InsuranceContract> findAll(ContractSearchCriteria criteria, SortPage sortPage) {
		Pageable pageable = createPageable(sortPage);
		Page<TravelContractEntity> contractPage = travelContractJpaRepository.findAll(createSpecification(criteria),
				pageable);

		if (contractPage.isEmpty()) {
			return PageResult.of(List.of(), 0, sortPage.size(), sortPage.page());
		}

		List<InsuranceContract> content = mapToContracts(contractPage.getContent());

		return new PageResult<>(content, contractPage.getTotalElements(), contractPage.getTotalPages(),
				contractPage.getNumber(), contractPage.hasNext());
	}

	private Pageable createPageable(SortPage sortPage) {
		Sort sort = Sort.by(Sort.Direction.DESC, Fields.ID);

		if (StringUtils.hasText(sortPage.sortBy())) {
			Sort.Direction direction = (sortPage.direction() != null && sortPage.direction().isAscending())
					? Sort.Direction.ASC : Sort.Direction.DESC;
			sort = Sort.by(direction, mapSortProperty(sortPage.sortBy()));
		}

		return PageRequest.of(sortPage.page(), sortPage.size(), sort);
	}

	private List<InsuranceContract> mapToContracts(List<TravelContractEntity> contracts) {
		List<Long> contractIds = contracts.stream().map(TravelContractEntity::getId).toList();
		List<Long> planIds = contracts.stream()
			.map(TravelContractEntity::getPlanId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();

		Map<Long, TravelInsurePaymentEntity> paymentMap = fetchPaymentMap(contractIds);
		Map<Long, TravelInsureRefundEntity> refundMap = fetchRefundMap(contractIds);
		Map<Long, List<TravelInsurePeopleEntity>> peopleMap = fetchPeopleMap(contractIds);
		Map<Long, TravelInsurancePlanEntity> planMap = fetchPlanMap(planIds);
		Map<Long, TravelInsurancePlanFamilyEntity> familyByPlanIdMap = fetchFamilyMapByPlanId(planIds);

		return contracts.stream()
			.map(c -> travelContractMapper.toDomain(c, paymentMap.get(c.getId()), refundMap.get(c.getId()),
					peopleMap.getOrDefault(c.getId(), Collections.emptyList()), planMap.get(c.getPlanId()),
					familyByPlanIdMap.get(c.getPlanId())))
			.toList();
	}

	private Map<Long, TravelInsurePaymentEntity> fetchPaymentMap(List<Long> contractIds) {
		return paymentJpaRepository.findByContractIdIn(contractIds)
			.stream()
			.collect(Collectors.toMap(TravelInsurePaymentEntity::getContractId, payment -> payment,
					(existing, replacement) -> existing));
	}

	private Map<Long, TravelInsureRefundEntity> fetchRefundMap(List<Long> contractIds) {
		return refundJpaRepository.findByContractIdIn(contractIds)
			.stream()
			.collect(Collectors.toMap(TravelInsureRefundEntity::getContractId, refund -> refund,
					(existing, replacement) -> existing));
	}

	private Map<Long, List<TravelInsurePeopleEntity>> fetchPeopleMap(List<Long> contractIds) {
		return insuredPersonJpaRepository.findAllByContractIdInAndDeletedAtIsNull(contractIds)
			.stream()
			.collect(Collectors.groupingBy(TravelInsurePeopleEntity::getContractId));
	}

	private Map<Long, TravelInsurancePlanEntity> fetchPlanMap(List<Long> planIds) {
		if (planIds.isEmpty()) {
			return Collections.emptyMap();
		}
		return planJpaRepository.findByIdIn(planIds)
			.stream()
			.collect(Collectors.toMap(TravelInsurancePlanEntity::getId, plan -> plan));
	}

	private Map<Long, TravelInsurancePlanFamilyEntity> fetchFamilyMapByPlanId(List<Long> planIds) {
		if (planIds.isEmpty()) {
			return Collections.emptyMap();
		}
		// planId → familyId 매핑
		List<TravelInsurancePlanFamilyMapEntity> maps = familyMapJpaRepository.findByPlanIdIn(planIds);
		Map<Long, Long> planToFamilyId = maps.stream()
			.collect(Collectors.toMap(TravelInsurancePlanFamilyMapEntity::getPlanId,
					TravelInsurancePlanFamilyMapEntity::getFamilyId, (a, b) -> a));

		// familyId로 family 엔티티 배치 조회
		List<Long> familyIds = planToFamilyId.values().stream().distinct().toList();
		Map<Long, TravelInsurancePlanFamilyEntity> familyMap = familyJpaRepository.findAllById(familyIds)
			.stream()
			.collect(Collectors.toMap(TravelInsurancePlanFamilyEntity::getId, f -> f));

		// planId → family 매핑
		return planToFamilyId.entrySet()
			.stream()
			.filter(e -> familyMap.containsKey(e.getValue()))
			.collect(Collectors.toMap(Map.Entry::getKey, e -> familyMap.get(e.getValue())));
	}

	@Override
	public Long save(InsuranceContract contract) {
		TravelContractEntity entity = travelContractJpaRepository.findById(contract.contractId())
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));

		applyContractChanges(entity, contract);

		// 피보험자 수 (동반자 수 기반으로 계산)
		entity.updateInsuredCount(contract.calculateTotalInsuredCount());

		TravelContractEntity saved = travelContractJpaRepository.save(entity);

		// 동반자 저장은 InsuredPeopleUpdater (Implement Layer)에서 처리
		savePayment(contract.contractId(), contract.paymentInfo());
		saveRefund(contract.contractId(), contract.refundInfo());

		return saved.getId();
	}

	@Override
	public List<InsuranceContract> findAll(ContractSearchCriteria criteria) {
		Specification<TravelContractEntity> spec = createSpecification(criteria);

		Sort sort = Sort.by(Sort.Direction.DESC, Fields.ID);

		List<TravelContractEntity> entities = travelContractJpaRepository.findAll(spec, sort);

		return mapToContracts(entities);
	}

	private void applyContractChanges(TravelContractEntity entity, InsuranceContract contract) {
		if (contract.status() != null) {
			entity.updateStatus(contract.status().name());
		}
		if (contract.applicant() != null) {
			entity.updateApplicant(contract.applicant());
		}
		if (contract.metaInfo() != null) {
			entity.updateInsurancePeriod(contract.metaInfo().period());
			entity.updateSubscriptionOrigin(contract.metaInfo().origin());
			entity.updatePolicyNumber(contract.metaInfo().policyNumber());
			entity.updatePolicyLink(contract.metaInfo().policyLink());
			entity.updateApplyDate(contract.metaInfo().applicationDate());
		}
		// 플랜 및 여행 국가 정보 수정
		if (contract.productPlan() != null) {
			entity.updatePlanId(contract.productPlan().planId());
			entity.updateCountryName(contract.productPlan().travelCountry());
			entity.updateCountryCode(contract.productPlan().countryCode());
		}
		// 납입보험료 수정
		if (contract.totalPremium() != null) {
			entity.updateTotalPremium(contract.totalPremium());
		}
		// 담당자 ID 수정
		if (contract.employeeId() != null) {
			entity.updateEmployeeId(contract.employeeId());
		}
	}

	private void savePayment(Long contractId, PaymentInfo paymentInfo) {
		if (paymentInfo == null) {
			return;
		}

		paymentJpaRepository.findByContractId(contractId).ifPresent(entity -> {
			entity.updatePaymentInfo(contractId, paymentInfo.status(), paymentInfo.method(), paymentInfo.totalAmount(),
					paymentInfo.paidAt(), paymentInfo.canceledAt());
			// Dirty Checking에 의해 트랜잭션 종료 시 업데이트 되지만, 명시적 save도 가능
			paymentJpaRepository.save(entity);
		});
	}

	private void saveRefund(Long contractId, RefundInfo refundInfo) {
		if (refundInfo == null) {
			return;
		}

		refundJpaRepository.findByContractId(contractId).ifPresentOrElse(entity -> {
			entity.update(refundInfo);
			refundJpaRepository.save(entity);
		}, () -> {
			// 환불 정보가 없으면 새로 생성 (paymentId 조회 필요)
			paymentJpaRepository.findByContractId(contractId).ifPresent(payment -> {
				TravelInsureRefundEntity newEntity = TravelInsureRefundEntity.create(payment.getId(), contractId,
						refundInfo);
				refundJpaRepository.save(newEntity);
			});
		});
	}

	private InsuranceContract fetchAndMapContract(Long contractId) {
		TravelContractEntity contract = travelContractJpaRepository.findById(contractId)
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));
		TravelInsurePaymentEntity payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
		TravelInsureRefundEntity refund = refundJpaRepository.findByContractId(contractId).orElse(null);
		List<TravelInsurePeopleEntity> people = insuredPersonJpaRepository
			.findAllByContractIdAndDeletedAtIsNull(contractId);
		TravelInsurancePlanEntity plan = fetchPlan(contract.getPlanId());
		TravelInsurancePlanFamilyEntity family = (contract.getPlanId() != null)
				? familyJpaRepository.findByPlanId(contract.getPlanId()).orElse(null) : null;

		return travelContractMapper.toDomain(contract, payment, refund, people, plan, family);
	}

	private String mapSortProperty(String requestProperty) {
		return switch (requestProperty) {
			case "startDate", "insuranceStartDate" -> Fields.INSURE_START_DATE;
			case "endDate", "insuranceEndDate" -> Fields.INSURE_END_DATE;
			case "applicationDate" -> Fields.APPLY_DATE;
			case "insuredCount", "insuredPeopleNumber" -> Fields.INSURED_PEOPLE_NUMBER;
			default -> requestProperty;
		};
	}

	private Specification<TravelContractEntity> createSpecification(ContractSearchCriteria criteria) {
		return Specification.where(notDeleted())
			.and(applyDateFrom(criteria))
			.and(applyDateTo(criteria))
			.and(partnerNameEquals(criteria))
			.and(channelNameEquals(criteria))
			.and(insurerNameEquals(criteria))
			.and(statusEquals(criteria))
			.and(applicantNameContains(criteria));
	}

	private Specification<TravelContractEntity> notDeleted() {
		return (root, query, cb) -> cb.isNull(root.get(Fields.DELETED_AT));
	}

	private Specification<TravelContractEntity> applyDateFrom(ContractSearchCriteria criteria) {
		return (root, query, cb) -> criteria.startDate() == null ? null
				: cb.greaterThanOrEqualTo(root.get(Fields.APPLY_DATE), criteria.startDate().atStartOfDay());
	}

	private Specification<TravelContractEntity> applyDateTo(ContractSearchCriteria criteria) {
		return (root, query, cb) -> criteria.endDate() == null ? null
				: cb.lessThan(root.get(Fields.APPLY_DATE), criteria.endDate().plusDays(1).atStartOfDay());
	}

	private Specification<TravelContractEntity> partnerNameEquals(ContractSearchCriteria criteria) {
		return (root, query, cb) -> StringUtils.hasText(criteria.partnerName())
				? cb.equal(root.get(Fields.PARTNER_NAME), criteria.partnerName()) : null;
	}

	private Specification<TravelContractEntity> channelNameEquals(ContractSearchCriteria criteria) {
		return (root, query, cb) -> StringUtils.hasText(criteria.channelName())
				? cb.equal(root.get(Fields.CHANNEL_NAME), criteria.channelName()) : null;
	}

	private Specification<TravelContractEntity> insurerNameEquals(ContractSearchCriteria criteria) {
		return (root, query, cb) -> StringUtils.hasText(criteria.insurerName())
				? cb.equal(root.get(Fields.INSURER_NAME), criteria.insurerName()) : null;
	}

	private Specification<TravelContractEntity> statusEquals(ContractSearchCriteria criteria) {
		return (root, query, cb) -> {
			LocalDate today = LocalDate.now();
			Predicate isCompleted = cb.equal(root.get(Fields.STATUS), ContractStatus.COMPLETED.name());

			// payment.status = 'CANCELED' 인 계약을 찾는 서브쿼리
			Subquery<Long> canceledPaymentSubquery = query.subquery(Long.class);
			Root<TravelInsurePaymentEntity> paymentRoot = canceledPaymentSubquery.from(TravelInsurePaymentEntity.class);
			canceledPaymentSubquery.select(paymentRoot.get("contractId"))
				.where(cb.equal(paymentRoot.get("contractId"), root.get(Fields.ID)),
						cb.equal(paymentRoot.get("status"), "CANCELED"));

			Predicate isPaymentCanceled = cb.exists(canceledPaymentSubquery);
			Predicate isEndDatePassed = cb.lessThan(root.get(Fields.INSURE_END_DATE), today);
			Predicate isEndDateNotPassed = cb.greaterThanOrEqualTo(root.get(Fields.INSURE_END_DATE), today);

			if (criteria.status() == null) {
				// 기본: 가입완료 + 임의해지 + 기간만료 (= contract.status가 COMPLETED인 모든 건)
				return isCompleted;
			}

			return switch (criteria.status()) {
				case COMPLETED -> cb.and(isCompleted, cb.not(isPaymentCanceled), isEndDateNotPassed);
				case CANCELED -> cb.and(isCompleted, isPaymentCanceled);
				case EXPIRED -> cb.and(isCompleted, cb.not(isPaymentCanceled), isEndDatePassed);
				default -> cb.equal(root.get(Fields.STATUS), criteria.status().name());
			};
		};
	}

	private Specification<TravelContractEntity> applicantNameContains(ContractSearchCriteria criteria) {
		return (root, query, cb) -> StringUtils.hasText(criteria.applicantName())
				? cb.like(root.get(Fields.APPLICANT_NAME), "%" + criteria.applicantName() + "%") : null;
	}

}