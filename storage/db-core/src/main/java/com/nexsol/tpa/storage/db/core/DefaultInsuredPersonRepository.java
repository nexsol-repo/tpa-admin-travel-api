package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.applicant.InsuredPersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DefaultInsuredPersonRepository implements InsuredPersonRepository {

	private final InsuredPersonJpaRepository insuredPersonJpaRepository;

	@Override
	public List<InsuredPerson> findAllByContractId(Long contractId) {
		return insuredPersonJpaRepository.findAllByContractIdAndDeletedAtIsNull(contractId)
			.stream()
			.map(TravelInsuredEntity::toDomain)
			.toList();
	}

	@Override
	public void softDeleteByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return;
		}

		List<TravelInsuredEntity> entities = insuredPersonJpaRepository.findAllById(ids);
		entities.forEach(TravelInsuredEntity::softDelete);
		insuredPersonJpaRepository.saveAll(entities);
	}

	@Override
	public void updateAll(List<InsuredPerson> people) {
		if (people == null || people.isEmpty()) {
			return;
		}

		List<Long> ids = people.stream().map(InsuredPerson::id).toList();

		List<TravelInsuredEntity> entities = insuredPersonJpaRepository.findAllById(ids);

		for (TravelInsuredEntity entity : entities) {
			people.stream()
				.filter(p -> p.id().equals(entity.getId()))
				.findFirst()
				.ifPresent(person -> entity.updatePersonInfo(person.name(), person.englishName(),
						person.residentNumber(), person.passportNumber(), person.gender(), person.phone(),
						person.email(), person.individualPremium()));
		}

		insuredPersonJpaRepository.saveAll(entities);
	}

	@Override
	public void createAll(Long contractId, List<InsuredPerson> people) {
		if (people == null || people.isEmpty()) {
			return;
		}

		List<TravelInsuredEntity> entities = people.stream()
			.map(person -> TravelInsuredEntity.create(contractId, person))
			.toList();

		insuredPersonJpaRepository.saveAll(entities);
	}

}
