package com.nexsol.tpa.core.domain.partner;

import com.nexsol.tpa.core.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 제휴사 조회 도구 클래스 (Implement Layer)
 */
@Component
@RequiredArgsConstructor
public class PartnerReader {

    private final PartnerRepository partnerRepository;

    public List<Partner> readAllActive() {
        return partnerRepository.findAllActive();
    }

    public List<Partner> readAllActiveByServiceType(ServiceType serviceType) {
        return partnerRepository.findAllActiveByServiceType(serviceType);
    }

}
