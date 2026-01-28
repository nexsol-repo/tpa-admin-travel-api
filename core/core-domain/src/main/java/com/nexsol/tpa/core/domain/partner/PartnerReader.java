package com.nexsol.tpa.core.domain.partner;

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

}
