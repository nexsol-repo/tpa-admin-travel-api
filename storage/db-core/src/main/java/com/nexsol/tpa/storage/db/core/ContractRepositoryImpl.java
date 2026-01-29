package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.contract.ContractRepository;
import com.nexsol.tpa.core.domain.contract.ContractSearchCriteria;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.Objects;
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

    }

    private final TravelContractJpaRepository travelContractJpaRepository;

    private final PaymentJpaRepository paymentJpaRepository;

    private final InsuredPersonJpaRepository insuredPersonJpaRepository;

    private final TravelInsurancePlanJpaRepository planJpaRepository;

    private final TravelContractMapper travelContractMapper;

    @Override
    public Optional<InsuranceContract> findById(Long contractId) {
        var contractEntityOptional = travelContractJpaRepository.findById(contractId);

        if (contractEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        TravelContractEntity contract = contractEntityOptional.get();

        var payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
        var people = insuredPersonJpaRepository.findAllByContractIdAndDeletedAtIsNull(contractId);
        var plan = fetchPlan(contract.getPlanId());

        return Optional.of(travelContractMapper.toDomain(contract, payment, people, plan));
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
            paymentJpaRepository.save(paymentEntity);
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
        Map<Long, List<TravelInsurePeopleEntity>> peopleMap = fetchPeopleMap(contractIds);
        Map<Long, TravelInsurancePlanEntity> planMap = fetchPlanMap(planIds);

        return contracts.stream()
            .map(c -> travelContractMapper.toDomain(c, paymentMap.get(c.getId()),
                    peopleMap.getOrDefault(c.getId(), Collections.emptyList()), planMap.get(c.getPlanId())))
            .toList();
    }

    private Map<Long, TravelInsurePaymentEntity> fetchPaymentMap(List<Long> contractIds) {
        return paymentJpaRepository.findByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.toMap(TravelInsurePaymentEntity::getContractId, payment -> payment,
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

    @Override
    public Long save(InsuranceContract contract) {
        TravelContractEntity entity = travelContractJpaRepository.findById(contract.contractId())
            .orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));

        applyContractChanges(entity, contract);

        // 피보험자 수
        entity.updateInsuredCount(contract.getTotalInsuredCount());

        TravelContractEntity saved = travelContractJpaRepository.save(entity);

        saveInsuredPeople(contract.contractId(), contract.insuredPeople());

        savePayment(contract.contractId(), contract.paymentInfo());

        return saved.getId();
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
        }
        // 플랜 ID 수정
        if (contract.productPlan() != null && contract.productPlan().planId() != null) {
            entity.updatePlanId(contract.productPlan().planId());
        }
        // 담당자 ID 수정
        if (contract.employeeId() != null) {
            entity.updateEmployeeId(contract.employeeId());
        }
    }

    private void saveInsuredPeople(Long contractId, List<InsuredPerson> insuredPeople) {
        // null이면 수정 안 함 (기존 유지)
        if (insuredPeople == null) {
            return;
        }

        // 기존 피보험자 조회 (삭제되지 않은 것만)
        List<TravelInsurePeopleEntity> existingPeople = insuredPersonJpaRepository
            .findAllByContractIdAndDeletedAtIsNull(contractId);

        // 요청에서 보낸 ID 목록
        Set<Long> requestedIds = insuredPeople.stream()
            .map(InsuredPerson::id)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        // 기존 피보험자 Map
        Map<Long, TravelInsurePeopleEntity> existingMap = existingPeople.stream()
            .collect(Collectors.toMap(TravelInsurePeopleEntity::getId, entity -> entity));

        // 1. 보내지 않은 기존 동반자는 soft delete
        for (TravelInsurePeopleEntity existing : existingPeople) {
            if (!requestedIds.contains(existing.getId())) {
                existing.softDelete();
            }
        }

        // 2. 보낸 동반자 업데이트 (기존에 있는 것만)
        for (InsuredPerson person : insuredPeople) {
            if (person.id() != null && existingMap.containsKey(person.id())) {
                TravelInsurePeopleEntity entity = existingMap.get(person.id());
                entity.updatePersonInfo(person.name(), person.englishName(), person.residentNumber(),
                        person.passportNumber(), person.gender());
            }
        }

        insuredPersonJpaRepository.saveAll(existingPeople);
    }

    private void savePayment(Long contractId, PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return;
        }

        paymentJpaRepository.findByContractId(contractId).ifPresent(entity -> {
            entity.updatePaymentInfo(contractId, paymentInfo.method(), paymentInfo.paidAt(), paymentInfo.canceledAt());
            // Dirty Checking에 의해 트랜잭션 종료 시 업데이트 되지만, 명시적 save도 가능
            paymentJpaRepository.save(entity);
        });
    }

    private InsuranceContract fetchAndMapContract(Long contractId) {
        TravelContractEntity contract = travelContractJpaRepository.findById(contractId)
            .orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));
        TravelInsurePaymentEntity payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
        List<TravelInsurePeopleEntity> people = insuredPersonJpaRepository
            .findAllByContractIdAndDeletedAtIsNull(contractId);
        TravelInsurancePlanEntity plan = fetchPlan(contract.getPlanId());

        return travelContractMapper.toDomain(contract, payment, people, plan);
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
        return Specification.where(applyDateFrom(criteria))
            .and(applyDateTo(criteria))
            .and(partnerNameEquals(criteria))
            .and(channelNameEquals(criteria))
            .and(insurerNameEquals(criteria))
            .and(statusEquals(criteria))
            .and(applicantNameContains(criteria));
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
        return (root, query, cb) -> criteria.status() == null ? null
                : cb.equal(root.get(Fields.STATUS), criteria.status().name());
    }

    private Specification<TravelContractEntity> applicantNameContains(ContractSearchCriteria criteria) {
        return (root, query, cb) -> StringUtils.hasText(criteria.applicantName())
                ? cb.like(root.get(Fields.APPLICANT_NAME), "%" + criteria.applicantName() + "%") : null;
    }

}