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
        // 1. PageRequest 생성 (최신순 정렬 기본 적용)
        Pageable pageable = PageRequest.of(sortPage.page(), sortPage.size(), Sort.by(Sort.Direction.DESC, "id"));

        // 2. 검색 조건(Specification) 생성 및 조회
        Specification<TravelContractEntity> spec = createSpecification(criteria);
        Page<TravelContractEntity> contractPage = travelContractJpaRepository.findAll(spec, pageable);

        List<TravelContractEntity> contracts = contractPage.getContent();
        if (contracts.isEmpty()) {
            // 빈 결과 반환 시에도 page() 메서드나 getNumber() 사용 가능
            return PageResult.of(List.of(), 0, sortPage.size(), sortPage.page());
        }

        // 3. 연관 데이터 조회 (연관관계를 맺지 않았으므로 ID 목록으로 별도 조회)
        List<Long> contractIds = contracts.stream().map(TravelContractEntity::getId).toList();

        // 3-1. 결제 정보 조회 및 매핑 (ContractId -> Payment)
        // 주의: PaymentJpaRepository에 findByContractIdIn 메서드가 필요합니다.
        Map<Long, TravelInsurePaymentEntity> paymentMap = paymentJpaRepository.findByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.toMap(TravelInsurePaymentEntity::getContractId, p -> p, (p1, p2) -> p1));

        // 3-2. 피보험자 정보 조회 및 매핑 (ContractId -> List<Person>)
        // 주의: InsuredPersonJpaRepository에 findAllByContractIdIn 메서드가 필요합니다.
        Map<Long, List<TravelInsurePeopleEntity>> peopleMap = insuredPersonJpaRepository
            .findAllByContractIdIn(contractIds)
            .stream()
            .collect(Collectors.groupingBy(TravelInsurePeopleEntity::getContractId));

        // 4. 도메인 객체로 변환 (Aggregate Root 생성)
        List<InsuranceContract> content = contracts.stream()
            .map(c -> mapper.toDomain(c, paymentMap.get(c.getId()), // 결제 정보가 없으면 null 전달됨
                    peopleMap.getOrDefault(c.getId(), Collections.emptyList()) // 피보험자 없으면
                                                                               // 빈 리스트
            ))
            .toList();

        // 5. PageResult 생성 (수정된 부분: getNumber() 사용)
        return new PageResult<>(content, contractPage.getTotalElements(), contractPage.getTotalPages(),
                contractPage.getNumber(), // [수정] getCurrentPage() -> getNumber()
                contractPage.hasNext());
    }

    @Override
    public InsuranceContract save(InsuranceContract contract) {
        return null;
    }

    private Specification<TravelContractEntity> createSpecification(ContractSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 기간 검색 (신청일 기준)
            if (criteria.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("applyDate"), criteria.startDate().atStartOfDay()));
            }
            if (criteria.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("applyDate"), criteria.endDate().atTime(LocalTime.MAX)));
            }

            // 키워드 검색
            if (StringUtils.hasText(criteria.keyword()) && StringUtils.hasText(criteria.keywordType())) {
                String keyword = "%" + criteria.keyword() + "%";
                if ("NAME".equalsIgnoreCase(criteria.keywordType())) {
                    predicates.add(cb.like(root.get("applicantName"), keyword));
                }
                else if ("PHONE".equalsIgnoreCase(criteria.keywordType())) {
                    predicates.add(cb.like(root.get("applicantPhone"), keyword));
                }
            }

            // TODO: status 필드는 현재 Entity에 없으므로 조건에서 제외하거나, Entity에 추가 후 구현해야 합니다.
            // if (criteria.status() != null) { ... }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
