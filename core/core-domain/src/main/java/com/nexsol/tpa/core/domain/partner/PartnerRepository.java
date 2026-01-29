package com.nexsol.tpa.core.domain.partner;

import com.nexsol.tpa.core.enums.ServiceType;

import java.util.List;

/**
 * 제휴사 Repository 인터페이스 - Business Layer에서 정의하고 Data Access Layer에서 구현
 */
public interface PartnerRepository {

    List<Partner> findAllActive();

    List<Partner> findAllActiveByServiceType(ServiceType serviceType);

}
