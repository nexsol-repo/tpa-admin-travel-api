package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.contract.ContractQueryRepository;
import com.nexsol.tpa.core.domain.contract.ContractSearchCriteria;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.support.PageResult;
import com.nexsol.tpa.core.support.SortPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DefaultContractQueryRepository implements ContractQueryRepository {

	private final TravelContractJooqRepository contractJooqRepository;

	private final TravelContractJpaRepository travelContractJpaRepository;

	private final PaymentJpaRepository paymentJpaRepository;

	private final InsuredPersonJpaRepository insuredPersonJpaRepository;

	private final TravelInsurancePlanJpaRepository planJpaRepository;

	private final TravelInsureRefundJpaRepository refundJpaRepository;

	private final TravelInsurancePlanFamilyJpaRepository familyJpaRepository;

	private final TravelContractMapper travelContractMapper;

	@Override
	public Optional<InsuranceContract> findById(Long contractId) {
		var contractEntity = travelContractJpaRepository.findById(contractId);
		if (contractEntity.isEmpty()) {
			return Optional.empty();
		}

		TravelContractEntity contract = contractEntity.get();
		var payment = paymentJpaRepository.findByContractId(contractId).orElse(null);
		var refund = refundJpaRepository.findByContractId(contractId).orElse(null);
		var people = insuredPersonJpaRepository.findAllByContractIdAndDeletedAtIsNull(contractId);
		var planMap = fetchPlanMapFromPeople(people);
		var family = (contract.getFamilyId() != null)
				? familyJpaRepository.findById(contract.getFamilyId()).orElse(null) : null;

		return Optional.of(travelContractMapper.toDomain(contract, payment, refund, people, planMap, family));
	}

	@Override
	public PageResult<InsuranceContract> findAll(ContractSearchCriteria criteria, SortPage sortPage) {
		int total = contractJooqRepository.count(criteria);

		if (total == 0) {
			return PageResult.of(List.of(), 0, sortPage.size(), sortPage.page());
		}

		List<Long> contractIds = contractJooqRepository.findIds(criteria, sortPage);
		List<InsuranceContract> content = mapToContracts(fetchOrderedEntities(contractIds));

		int totalPages = (int) Math.ceil((double) total / sortPage.size());

		return new PageResult<>(content, total, totalPages, sortPage.page(), sortPage.page() < totalPages - 1);
	}

	@Override
	public List<InsuranceContract> findAll(ContractSearchCriteria criteria) {
		List<Long> contractIds = contractJooqRepository.findIds(criteria);
		return mapToContracts(fetchOrderedEntities(contractIds));
	}

	private List<TravelContractEntity> fetchOrderedEntities(List<Long> contractIds) {
		Map<Long, TravelContractEntity> entityMap = travelContractJpaRepository.findAllById(contractIds)
			.stream()
			.collect(Collectors.toMap(TravelContractEntity::getId, e -> e));

		return contractIds.stream().map(entityMap::get).filter(Objects::nonNull).toList();
	}

	private List<InsuranceContract> mapToContracts(List<TravelContractEntity> contracts) {
		List<Long> contractIds = contracts.stream().map(TravelContractEntity::getId).toList();
		List<Long> familyIds = contracts.stream()
			.map(TravelContractEntity::getFamilyId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();

		Map<Long, TravelPaymentEntity> paymentMap = fetchPaymentMap(contractIds);
		Map<Long, TravelRefundEntity> refundMap = fetchRefundMap(contractIds);
		Map<Long, List<TravelInsuredEntity>> peopleMap = fetchPeopleMap(contractIds);
		Map<Long, TravelInsurancePlanFamilyEntity> familyMap = fetchFamilyMap(familyIds);

		List<Long> allPlanIds = peopleMap.values()
			.stream()
			.flatMap(List::stream)
			.map(TravelInsuredEntity::getPlanId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();
		Map<Long, TravelInsurancePlanEntity> planMap = fetchPlanMap(allPlanIds);

		return contracts.stream()
			.map(c -> travelContractMapper.toDomain(c, paymentMap.get(c.getId()), refundMap.get(c.getId()),
					peopleMap.getOrDefault(c.getId(), Collections.emptyList()), planMap,
					familyMap.get(c.getFamilyId())))
			.toList();
	}

	private Map<Long, TravelInsurancePlanEntity> fetchPlanMapFromPeople(List<TravelInsuredEntity> people) {
		List<Long> planIds = people.stream()
			.map(TravelInsuredEntity::getPlanId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();
		return fetchPlanMap(planIds);
	}

	private Map<Long, TravelPaymentEntity> fetchPaymentMap(List<Long> contractIds) {
		return paymentJpaRepository.findByContractIdIn(contractIds)
			.stream()
			.collect(Collectors.toMap(TravelPaymentEntity::getContractId, p -> p, (a, b) -> a));
	}

	private Map<Long, TravelRefundEntity> fetchRefundMap(List<Long> contractIds) {
		return refundJpaRepository.findByContractIdIn(contractIds)
			.stream()
			.collect(Collectors.toMap(TravelRefundEntity::getContractId, r -> r, (a, b) -> a));
	}

	private Map<Long, List<TravelInsuredEntity>> fetchPeopleMap(List<Long> contractIds) {
		return insuredPersonJpaRepository.findAllByContractIdInAndDeletedAtIsNull(contractIds)
			.stream()
			.collect(Collectors.groupingBy(TravelInsuredEntity::getContractId));
	}

	private Map<Long, TravelInsurancePlanEntity> fetchPlanMap(List<Long> planIds) {
		if (planIds.isEmpty()) {
			return Collections.emptyMap();
		}
		return planJpaRepository.findByIdIn(planIds)
			.stream()
			.collect(Collectors.toMap(TravelInsurancePlanEntity::getId, p -> p));
	}

	private Map<Long, TravelInsurancePlanFamilyEntity> fetchFamilyMap(List<Long> familyIds) {
		if (familyIds.isEmpty()) {
			return Collections.emptyMap();
		}
		return familyJpaRepository.findAllById(familyIds)
			.stream()
			.collect(Collectors.toMap(TravelInsurancePlanFamilyEntity::getId, f -> f));
	}

}