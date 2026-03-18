package com.nexsol.tpa.core.domain.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 플랜 패밀리 조회 도구 클래스 (Implement Layer)
 * 플랜명 + 실손여부로 패밀리명을 조회한다.
 * 70세 이상을 제외하면 default suffix는 "B", 실손제외도 "B"
 */
@Component
@RequiredArgsConstructor
public class PlanFamilyReader {

	private static final String DEFAULT_SUFFIX = "B";

	private final PlanFamilyRepository planFamilyRepository;

	/**
	 * 플랜 표시명과 실손여부로 패밀리명을 조회한다.
	 * @param planName 플랜 표시명 (예: "가뿐한플랜")
	 * @param isLoss 실손 포함 여부
	 * @return 패밀리명 (예: "가뿐한플랜B")
	 */
	public Optional<String> findFamilyName(String planName, boolean isLoss) {
		String familyName = planName + DEFAULT_SUFFIX;
		return planFamilyRepository.findFamilyNameByExactName(familyName, isLoss);
	}

}