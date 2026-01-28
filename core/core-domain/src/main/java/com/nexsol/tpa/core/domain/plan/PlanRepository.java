package com.nexsol.tpa.core.domain.plan;

import java.util.List;
import java.util.Optional;

/**
 * 플랜 Repository 인터페이스 - Business Layer에서 정의하고 Data Access Layer에서 구현
 */
public interface PlanRepository {

    Optional<Plan> findById(Long id);

    List<Plan> findAllActive();

    List<Plan> findAllActiveByInsurerId(Long insurerId);

}
