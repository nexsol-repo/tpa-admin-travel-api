package com.nexsol.tpa.core.enums;

import lombok.Getter;

@Getter
public enum PlanGrade {

	A("A"), B("B");

	private final String suffix;

	PlanGrade(String suffix) {
		this.suffix = suffix;
	}

	public static PlanGrade defaultGrade() {
		return B;
	}

}