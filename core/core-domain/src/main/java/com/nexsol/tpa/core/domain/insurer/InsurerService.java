package com.nexsol.tpa.core.domain.insurer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 보험사 서비스 (Business Layer) - 비즈니스 흐름을 중계하는 Coordinator 역할
 */
@Service
@RequiredArgsConstructor
public class InsurerService {

	private final InsurerReader insurerReader;

	public List<Insurer> getActiveInsurers() {
		return insurerReader.readAllActive();
	}

}