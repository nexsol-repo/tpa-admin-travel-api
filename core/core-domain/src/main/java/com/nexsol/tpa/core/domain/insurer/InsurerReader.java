package com.nexsol.tpa.core.domain.insurer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 보험사 조회 도구 클래스 (Implement Layer)
 */
@Component
@RequiredArgsConstructor
public class InsurerReader {

	private final InsurerRepository insurerRepository;

	public List<Insurer> readAllActive() {
		return insurerRepository.findAllActive();
	}

}
