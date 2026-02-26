package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.insurer.Insurer;

public record InsurerResponse(Long id, String value, String label) {

	public static InsurerResponse of(Insurer insurer) {
		return new InsurerResponse(insurer.id(), insurer.code(), insurer.name());
	}

}
