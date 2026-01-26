package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.ContractRepository;
import com.nexsol.tpa.core.domain.ContractSearchCriteria;
import com.nexsol.tpa.core.domain.InsuranceContract;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepository {

    private final TravelContractJpaRepository travelContractJpaRepository;

    private final TravelInsurerJpaRepository insurerJpaRepository;

    private final PaymentJpaRepository paymentJpaRepository;

    private final InsuredPersonJpaRepository insuredPersonJpaRepository;

    private final PartnerJpaRepository partnerJpaRepository;

    private final ChannelJpaRepository channelJpaRepository;

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

        // [수정] 단건 조회 시에도 이름 정보 조회 (조회 실패 시 ID를 문자로 대체)
        String partnerName = partnerJpaRepository.findById(contract.getPartnerId())
            .map(TravelPartnerEntity::getPartnerName)
            .orElse(String.valueOf(contract.getPartnerId()));

        String channelName = channelJpaRepository.findById(contract.getChannelId())
            .map(TravelChannelEntity::getChannelName)
            .orElse(String.valueOf(contract.getChannelId()));

        String insurerName = insurerJpaRepository.findById(contract.getInsurerId())
            .map(TravelInsurerEntity::getInsurerName)
            .orElse(String.valueOf(contract.getInsurerId()));

        return Optional.of(mapper.toDomain(contract, payment, people, partnerName, channelName, insurerName));
    }

    @Override
    public PageResult<InsuranceContract> findAll(ContractSearchCriteria criteria, SortPage sortPage) {
        // 1. 계약 조회
        Pageable pageable = PageRequest.of(sortPage.page(), sortPage.size(), Sort.by(Sort.Direction.DESC, "id"));
        Specification<TravelContractEntity> spec = createSpecification(criteria);
        Page<TravelContractEntity> contractPage = travelContractJpaRepository.findAll(spec, pageable);
        List<TravelContractEntity> contracts = contractPage.getContent();

        if (contracts.isEmpty()) {
            return PageResult.of(List.of(), 0, sortPage.size(), sortPage.page());
        }

        // 2. ID 추출
        List<Long> contractIds = contracts.stream().map(TravelContractEntity::getId).toList();
        List<Long> partnerIds = contracts.stream().map(TravelContractEntity::getPartnerId).distinct().toList();
        List<Long> channelIds = contracts.stream().map(TravelContractEntity::getChannelId).distinct().toList();
        List<Long> insurerIds = contracts.stream().map(TravelContractEntity::getInsurerId).distinct().toList();

        // 3. 데이터 별도 조회
        Map<Long, TravelInsurePaymentEntity> paymentMap = paymentJpaRepository.findByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.toMap(TravelInsurePaymentEntity::getContractId, p -> p, (p1, p2) -> p1));

        Map<Long, List<TravelInsurePeopleEntity>> peopleMap = insuredPersonJpaRepository
            .findAllByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.groupingBy(TravelInsurePeopleEntity::getContractId));

        Map<Long, String> partnerNameMap = partnerJpaRepository.findAllById(partnerIds)
            .stream()
            .collect(Collectors.toMap(TravelPartnerEntity::getId, TravelPartnerEntity::getPartnerName));

        Map<Long, String> channelNameMap = channelJpaRepository.findAllById(channelIds)
            .stream()
            .collect(Collectors.toMap(TravelChannelEntity::getId, TravelChannelEntity::getChannelName));

        Map<Long, String> insurerNameMap = insurerJpaRepository.findAllById(insurerIds)
            .stream()
            .collect(Collectors.toMap(TravelInsurerEntity::getId, TravelInsurerEntity::getInsurerName));

        // 4. 도메인 변환
        List<InsuranceContract> content = contracts.stream()
            .map(c -> mapper.toDomain(c, paymentMap.get(c.getId()),
                    peopleMap.getOrDefault(c.getId(), Collections.emptyList()),
                    partnerNameMap.getOrDefault(c.getPartnerId(), String.valueOf(c.getPartnerId())),
                    channelNameMap.getOrDefault(c.getChannelId(), String.valueOf(c.getChannelId())),
                    // [수정] 누락되었던 insurerName 인자 추가
                    insurerNameMap.getOrDefault(c.getInsurerId(), String.valueOf(c.getInsurerId()))))
            .toList();

        return new PageResult<>(content, contractPage.getTotalElements(), contractPage.getTotalPages(),
                contractPage.getNumber(), contractPage.hasNext());
    }

    @Override
    public InsuranceContract save(InsuranceContract contract) {
        return null;
    }

    private Specification<TravelContractEntity> createSpecification(ContractSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyDate"), criteria.startDate().atStartOfDay()));
            }
            if (criteria.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyDate"), criteria.endDate().atTime(LocalTime.MAX)));
            }

            if (StringUtils.hasText(criteria.keyword()) && StringUtils.hasText(criteria.keywordType())) {
                String keyword = "%" + criteria.keyword() + "%";
                if ("NAME".equalsIgnoreCase(criteria.keywordType())) {
                    predicates.add(cb.like(root.get("applicantName"), keyword));
                }
                else if ("PHONE".equalsIgnoreCase(criteria.keywordType())) {
                    predicates.add(cb.like(root.get("applicantPhone"), keyword));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}