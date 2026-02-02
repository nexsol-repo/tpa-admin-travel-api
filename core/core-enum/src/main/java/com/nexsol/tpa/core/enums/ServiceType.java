package com.nexsol.tpa.core.enums;

import lombok.Getter;

/**
 * 서비스 유형 열거형
 */
@Getter
public enum ServiceType {

	PUNGSU("풍수"), TRAVEL("여행"), SOLAR("태양광");

	private final String description;

	ServiceType(String description) {
		this.description = description;
	}

}
