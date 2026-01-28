package com.nexsol.tpa.core.domain.plan;

import java.util.List;

/**
 * 플랜 Repository 인터페이스 - Business Layer에서 정의하고 Data Access Layer에서 구현
 */
public interface PlanRepository {

    List<Plan> findAllActive();

    List<Plan> findAllActiveByInsurerId(Long insurerId);

}
