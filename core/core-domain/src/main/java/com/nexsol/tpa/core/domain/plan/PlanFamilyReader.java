package com.nexsol.tpa.core.domain.plan;

import java.util.Optional;

/**
 * 플랜 패밀리 조회 인터페이스 - is_loss 조건으로 패밀리명을 조회한다.
 */
public interface PlanFamilyReader {

	/**
	 * 플랜명 prefix와 is_loss 조건으로 패밀리명을 조회한다.
	 * @param planName 플랜 표시명 (예: "가뿐한플랜")
	 * @param isLoss 실손 포함 여부
	 * @return 패밀리명 (예: "가뿐한플랜A", "맘편한플랜B")
	 */
	Optional<String> findFamilyName(String planName, boolean isLoss);

}