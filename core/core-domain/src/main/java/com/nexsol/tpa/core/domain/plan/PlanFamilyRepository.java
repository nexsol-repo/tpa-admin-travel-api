package com.nexsol.tpa.core.domain.plan;

import java.util.Optional;

/**
 * 플랜 패밀리 Repository 인터페이스 - Business Layer에서 정의하고 Data Access Layer에서 구현
 */
public interface PlanFamilyRepository {

	Optional<String> findFamilyNameByExactName(String familyName, boolean isLoss);

}