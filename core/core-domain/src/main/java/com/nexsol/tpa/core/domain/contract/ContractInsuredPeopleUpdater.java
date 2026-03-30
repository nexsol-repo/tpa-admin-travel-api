package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 피보험자 목록 수정을 담당하는 도구 클래스 (Implement Layer) - 기존 목록과 수정 요청 병합 - 대표계약자 정보 동기화
 */
@Component
public class ContractInsuredPeopleUpdater {

	/**
	 * 기존 피보험자 목록과 수정 요청을 병합한다.
	 */
	public List<InsuredPerson> merge(List<InsuredPerson> existing, List<ModifyInsuredPerson> modifications) {
		if (modifications == null) {
			return existing;
		}
		if (modifications.isEmpty()) {
			return List.of();
		}

		Map<Long, InsuredPerson> existingMap = existing.stream()
			.filter(p -> p.id() != null)
			.collect(Collectors.toMap(InsuredPerson::id, p -> p));

		List<InsuredPerson> result = modifications.stream()
			.map(m -> toInsuredPerson(m, existingMap.get(m.id())))
			.collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);

		preserveUnmodifiedContractor(existing, modifications, result);

		return result;
	}

	/**
	 * 신청자(Applicant) 정보를 대표계약자(isContractor=true) 피보험자에 동기화한다.
	 */
	public List<InsuredPerson> syncContractorFromApplicant(List<InsuredPerson> insuredPeople, Applicant applicant) {
		if (applicant == null || insuredPeople == null) {
			return insuredPeople;
		}
		return insuredPeople.stream().map(person -> {
			if (!Boolean.TRUE.equals(person.isContractor())) {
				return person;
			}
			return InsuredPerson.builder()
				.id(person.id())
				.planId(person.planId())
				.isContractor(true)
				.name(applicant.name())
				.residentNumber(applicant.residentNumber())
				.phone(applicant.phoneNumber())
				.email(applicant.email())
				.englishName(person.englishName())
				.passportNumber(person.passportNumber())
				.gender(person.gender())
				.individualPremium(person.individualPremium())
				.build();
		}).toList();
	}

	/**
	 * 기존 contractor가 수정 목록에 없으면 보존
	 */
	private void preserveUnmodifiedContractor(List<InsuredPerson> existing, List<ModifyInsuredPerson> modifications,
			List<InsuredPerson> result) {

		Set<Long> modifiedIds = modifications.stream()
			.map(ModifyInsuredPerson::id)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		existing.stream()
			.filter(p -> Boolean.TRUE.equals(p.isContractor()))
			.filter(p -> !modifiedIds.contains(p.id()))
			.findFirst()
			.ifPresent(result::add);
	}

	private InsuredPerson toInsuredPerson(ModifyInsuredPerson m, InsuredPerson existingPerson) {
		Long planId = null;
		Boolean isContractor = false;
		if (existingPerson != null) {
			planId = existingPerson.planId();
			isContractor = existingPerson.isContractor();
		}

		return InsuredPerson.builder()
			.id(m.id())
			.planId(planId)
			.isContractor(isContractor)
			.name(m.name())
			.englishName(m.englishName())
			.residentNumber(m.residentNumber())
			.passportNumber(m.passportNumber())
			.gender(m.gender())
			.individualPremium(m.premium())
			.build();
	}

}