package com.nexsol.tpa.core.domain.applicant;

import java.util.List;

/**
 * 피보험자(동반자) 변경 내역을 담는 도메인 개념 객체
 */
public record InsuredPeopleChanges(List<Long> toDelete, List<InsuredPerson> toUpdate, List<InsuredPerson> toCreate) {

	public boolean hasChanges() {
		return !toDelete.isEmpty() || !toUpdate.isEmpty() || !toCreate.isEmpty();
	}

	public boolean isEmpty() {
		return toDelete.isEmpty() && toUpdate.isEmpty() && toCreate.isEmpty();
	}

}
