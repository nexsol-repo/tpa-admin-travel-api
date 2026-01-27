package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.ContractRepository;
import com.nexsol.tpa.core.domain.ContractSearchCriteria;
import com.nexsol.tpa.core.domain.InsuranceContract;
import com.nexsol.tpa.core.domain.PaymentInfo;
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

    private final ContractEntityMapper mapper;

    @Override
    public Optional<InsuranceContract> findById(Long contractId) {
        var contractEntityOptional = travelContractJpaRepository.findById(contractId);

        if (contractEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        TravelContractEntity contract = contractEntityOptional.get();

        var payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
        var people = insuredPersonJpaRepository.findAllByContractId(contractId);

        return Optional.of(mapper.toDomain(contract, payment, people));
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

        Map<Long, TravelInsurePaymentEntity> paymentMap = fetchPaymentMap(contractIds);
        Map<Long, List<TravelInsurePeopleEntity>> peopleMap = fetchPeopleMap(contractIds);

        return contracts.stream()
            .map(c -> mapper.toDomain(c, paymentMap.get(c.getId()),
                    peopleMap.getOrDefault(c.getId(), Collections.emptyList())))
            .toList();
    }

    private Map<Long, TravelInsurePaymentEntity> fetchPaymentMap(List<Long> contractIds) {
        return paymentJpaRepository.findByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.toMap(TravelInsurePaymentEntity::getContractId, payment -> payment,
                    (existing, replacement) -> existing));
    }

    private Map<Long, List<TravelInsurePeopleEntity>> fetchPeopleMap(List<Long> contractIds) {
        return insuredPersonJpaRepository.findAllByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.groupingBy(TravelInsurePeopleEntity::getContractId));
    }

    @Override
    public InsuranceContract save(InsuranceContract contract) {
        TravelContractEntity entity = travelContractJpaRepository.findById(contract.contractId())
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contract.contractId()));

        applyContractChanges(entity, contract);

        if (contract.insuredPeople() != null) {
            entity.updateInsuredCount(contract.insuredPeople().size());
        }

        TravelContractEntity saved = travelContractJpaRepository.save(entity);

        saveInsuredPeople(contract.contractId(), contract.insuredPeople());

        savePayment(contract.contractId(), contract.paymentInfo());

        return fetchAndMapContract(saved.getId());
    }

    private void applyContractChanges(TravelContractEntity entity, InsuranceContract contract) {
        if (contract.status() != null) {
            entity.updateStatus(contract.status().name());
        }
        if (contract.applicant() != null) {
            entity.updateApplicant(contract.applicant().name(), contract.applicant().phoneNumber(),
                    contract.applicant().email());
        }
        if (contract.metaInfo() != null && contract.metaInfo().period() != null) {
            entity.updateInsurancePeriod(contract.metaInfo().period().startDate(),
                    contract.metaInfo().period().endDate());
        }
    }

    private void saveInsuredPeople(Long contractId, List<com.nexsol.tpa.core.domain.InsuredPerson> insuredPeople) {
        if (insuredPeople == null || insuredPeople.isEmpty()) {
            return;
        }

        List<TravelInsurePeopleEntity> existingPeople = insuredPersonJpaRepository.findAllByContractId(contractId);

        for (int i = 0; i < insuredPeople.size() && i < existingPeople.size(); i++) {
            var person = insuredPeople.get(i);
            var entity = existingPeople.get(i);
            entity.updatePersonInfo(person.name(), person.englishName(), person.residentNumber(),
                    person.passportNumber(), person.gender());
        }

        insuredPersonJpaRepository.saveAll(existingPeople);
    }

    private void savePayment(Long contractId, PaymentInfo paymentInfo) {
        if (paymentInfo == null) {
            return;
        }

        paymentJpaRepository.findByContractId(contractId).ifPresent(entity -> {
            entity.updatePaymentInfo(contractId,paymentInfo.method(), paymentInfo.paidAt(), paymentInfo.canceledAt());
            // Dirty Checking에 의해 트랜잭션 종료 시 업데이트 되지만, 명시적 save도 가능
            paymentJpaRepository.save(entity);
        });
    }

    private InsuranceContract fetchAndMapContract(Long contractId) {
        TravelContractEntity contract = travelContractJpaRepository.findById(contractId)
            .orElseThrow(() -> new IllegalArgumentException("Contract not found: " + contractId));
        TravelInsurePaymentEntity payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
        List<TravelInsurePeopleEntity> people = insuredPersonJpaRepository.findAllByContractId(contractId);

        return mapper.toDomain(contract, payment, people);
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