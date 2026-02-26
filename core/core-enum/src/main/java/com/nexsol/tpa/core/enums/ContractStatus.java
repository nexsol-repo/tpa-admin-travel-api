package com.nexsol.tpa.core.enums;

import lombok.Getter;

@Getter
public enum ContractStatus {

	PENDING("접수"), COMPLETED("가입완료"), // UI의 '가입완료'
	CANCELED("임의해지"), // UI의 '해지'
	EXPIRED("기간만료"), ERROR("가입오류");

	private final String description;

	ContractStatus(String description) {
		this.description = description;
	}

}