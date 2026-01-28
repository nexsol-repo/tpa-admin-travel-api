package com.nexsol.tpa.core.domain.insurer;

import java.util.List;

/**
 * 보험사 Repository 인터페이스 - Business Layer에서 정의하고 Data Access Layer에서 구현
 */
public interface InsurerRepository {

    List<Insurer> findAllActive();

}
