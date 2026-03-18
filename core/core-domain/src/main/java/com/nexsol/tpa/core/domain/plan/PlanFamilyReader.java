package com.nexsol.tpa.core.domain.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 플랜 패밀리 조회 도구 클래스 (Implement Layer)
 * 플랜명 + 실손여부로 패밀리명을 조회한다.
 * - 실손포함: "가뿐한플랜B" (is_loss=1)
 * - 실손제외: "가뿐한플랜B 실손제외" (is_loss=0)
 */
@Component
@RequiredArgsConstructor
public class PlanFamilyReader {

	private static final String DEFAULT_SUFFIX = "B";

	private static final String SILSON_EXCLUDE_SUFFIX = " 실손제외";

	private final PlanFamilyRepository planFamilyRepository;

	/**
	 * 플랜 표시명과 실손여부로 패밀리명을 조회한다.
	 * @param planName 플랜 표시명 (예: "가뿐한플랜")
	 * @param isLoss 실손 포함 여부 (true: 실손포함, false: 실손제외)
	 * @return 패밀리명 (예: "가뿐한플랜B" 또는 "가뿐한플랜B 실손제외")
	 */
	public Optional<String> findFamilyName(String planName, boolean isLoss) {
		String familyName = planName + DEFAULT_SUFFIX;
		if (!isLoss) {
			familyName += SILSON_EXCLUDE_SUFFIX;
		}
		return planFamilyRepository.findFamilyNameByExactName(familyName, isLoss);
	}

}