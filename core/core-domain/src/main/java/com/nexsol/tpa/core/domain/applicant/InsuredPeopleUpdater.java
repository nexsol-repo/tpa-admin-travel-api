package com.nexsol.tpa.core.domain.applicant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 피보험자(동반자) 수정 도구 클래스 (Implement Layer) 동반자 변경 감지 및 적용 로직을 담당
 */
@Component
@RequiredArgsConstructor
public class InsuredPeopleUpdater {

    private final InsuredPersonRepository insuredPersonRepository;

    /**
     * 동반자 목록 수정
     * @param contractId 계약 ID
     * @param requested 요청된 동반자 목록 (null: 수정 안 함, 빈 리스트: 전체 삭제)
     */
    public void update(Long contractId, List<InsuredPerson> requested) {
        if (requested == null) {
            return;
        }

        List<InsuredPerson> existing = insuredPersonRepository.findAllByContractId(contractId);
        InsuredPeopleChanges changes = detectChanges(existing, requested);
        applyChanges(contractId, changes);
    }

    /**
     * 기존 목록과 요청 목록을 비교하여 변경 내역 감지
     */
    private InsuredPeopleChanges detectChanges(List<InsuredPerson> existing, List<InsuredPerson> requested) {
        Set<Long> requestedIds = extractIds(requested);
        Map<Long, InsuredPerson> existingMap = toMap(existing);

        // 삭제 대상: 기존에 있지만 요청에 없는 것
        List<Long> toDelete = existing.stream()
            .map(InsuredPerson::id)
            .filter(id -> !requestedIds.contains(id))
            .toList();

        // 수정 대상: 요청에 id가 있고, 기존에도 존재하는 것
        List<InsuredPerson> toUpdate = requested.stream()
            .filter(p -> p.id() != null && existingMap.containsKey(p.id()))
            .toList();

        // 생성 대상: 요청에 id가 없는 것
        List<InsuredPerson> toCreate = requested.stream().filter(p -> p.id() == null).toList();

        return new InsuredPeopleChanges(toDelete, toUpdate, toCreate);
    }

    /**
     * 변경 내역 적용
     */
    private void applyChanges(Long contractId, InsuredPeopleChanges changes) {
        if (!changes.toDelete().isEmpty()) {
            insuredPersonRepository.softDeleteByIds(changes.toDelete());
        }

        if (!changes.toUpdate().isEmpty()) {
            insuredPersonRepository.updateAll(changes.toUpdate());
        }

        if (!changes.toCreate().isEmpty()) {
            insuredPersonRepository.createAll(contractId, changes.toCreate());
        }
    }

    private Set<Long> extractIds(List<InsuredPerson> people) {
        return people.stream().map(InsuredPerson::id).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private Map<Long, InsuredPerson> toMap(List<InsuredPerson> people) {
        return people.stream().filter(p -> p.id() != null).collect(Collectors.toMap(InsuredPerson::id, p -> p));
    }

}
