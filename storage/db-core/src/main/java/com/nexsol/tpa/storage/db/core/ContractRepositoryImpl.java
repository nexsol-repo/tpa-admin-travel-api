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

    private final PaymentJpaRepository paymentJpaRepository;

    private final InsuredPersonJpaRepository insuredPersonJpaRepository;

    private final ContractEntityMapper mapper;

    // [Refactored] Partner, Channel, Insurer Repository 제거됨

    @Override
    public Optional<InsuranceContract> findById(Long contractId) {
        var contractEntityOptional = travelContractJpaRepository.findById(contractId);

        if (contractEntityOptional.isEmpty()) {
            return Optional.empty();
        }

        TravelContractEntity contract = contractEntityOptional.get();

        var payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
        var people = insuredPersonJpaRepository.findAllByContractId(contractId);

        // [Refactored] Name 조회를 위한 추가 쿼리 제거
        return Optional.of(mapper.toDomain(contract, payment, people));
    }

    @Override
    public PageResult<InsuranceContract> findAll(ContractSearchCriteria criteria, SortPage sortPage) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id"); // 기본값: 최신순

        if (StringUtils.hasText(sortPage.sortBy())) {
            String entityProperty = mapSortProperty(sortPage.sortBy());

            Sort.Direction dir = (sortPage.direction() != null && sortPage.direction().isAscending())
                    ? Sort.Direction.ASC : Sort.Direction.DESC;

            sort = Sort.by(dir, entityProperty);
        }

        // 2. Pageable 생성
        Pageable pageable = PageRequest.of(sortPage.page(), sortPage.size(), sort);

        // 3. 쿼리 실행 (Specification + Pageable)
        // JPA가 알아서 "ORDER BY partner_name ASC" 쿼리를 생성함
        Page<TravelContractEntity> contractPage = travelContractJpaRepository.findAll(createSpecification(criteria),
                pageable);

        List<TravelContractEntity> contracts = contractPage.getContent();

        if (contracts.isEmpty()) {
            return PageResult.of(List.of(), 0, sortPage.size(), sortPage.page());
        }

        // 2. ID 추출 (Payment, People 조회용)
        List<Long> contractIds = contracts.stream().map(TravelContractEntity::getId).toList();

        // 3. 연관 데이터 조회 (Payment, People)
        Map<Long, TravelInsurePaymentEntity> paymentMap = paymentJpaRepository.findByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.toMap(TravelInsurePaymentEntity::getContractId, p -> p, (p1, p2) -> p1));

        Map<Long, List<TravelInsurePeopleEntity>> peopleMap = insuredPersonJpaRepository
            .findAllByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.groupingBy(TravelInsurePeopleEntity::getContractId));

        // 4. 도메인 변환
        List<InsuranceContract> content = contracts.stream()
            .map(c -> mapper.toDomain(c, paymentMap.get(c.getId()),
                    peopleMap.getOrDefault(c.getId(), Collections.emptyList())))
            .toList();

        return new PageResult<>(content, contractPage.getTotalElements(), contractPage.getTotalPages(),
                contractPage.getNumber(), contractPage.hasNext());
    }

    @Override
    public InsuranceContract save(InsuranceContract contract) {
        // TODO: 저장 로직 구현 시 Entity의 partnerName, partnerCode 등도 설정 필요
        return null;
    }

    private String mapSortProperty(String requestProperty) {
        return switch (requestProperty) {
            case "startDate", "insuranceStartDate" -> "insureStartDate"; // Entity 필드명
            case "endDate", "insuranceEndDate" -> "insureEndDate"; // Entity 필드명
            case "applicationDate" -> "applyDate"; // 신청일
            default -> requestProperty; // 나머지는 이름이 같으므로 그대로 사용 (partnerName 등)
        };
    }

    private Specification<TravelContractEntity> createSpecification(ContractSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 기간 검색 (신청일 기준)
            if (criteria.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyDate"), criteria.startDate().atStartOfDay()));
            }
            if (criteria.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyDate"), criteria.endDate().atTime(LocalTime.MAX)));
            }

            // 2. 드롭다운 필터 (Name 일치 검색)
            // [Refactored] Code가 아닌 Name 컬럼 사용
            if (StringUtils.hasText(criteria.partnerName())) {
                predicates.add(cb.equal(root.get("partnerName"), criteria.partnerName()));
            }
            if (StringUtils.hasText(criteria.channelName())) {
                predicates.add(cb.equal(root.get("channelName"), criteria.channelName()));
            }
            if (StringUtils.hasText(criteria.insurerName())) {
                predicates.add(cb.equal(root.get("insurerName"), criteria.insurerName()));
            }

            // 3. 상태 검색
            if (criteria.status() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.status().name()));
            }

            // 4. 가입자명 검색 (Like 검색)
            // [Refactored] keywordType 제거 -> applicantName 고정
            if (StringUtils.hasText(criteria.applicantName())) {
                String keyword = "%" + criteria.applicantName() + "%";
                predicates.add(cb.like(root.get("applicantName"), keyword));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}