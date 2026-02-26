package com.nexsol.tpa.core.domain.partner;

import com.nexsol.tpa.core.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 제휴사 서비스 (Business Layer) - 비즈니스 흐름을 중계하는 Coordinator 역할
 */
@Service
@RequiredArgsConstructor
public class PartnerService {

	private final PartnerReader partnerReader;

	public List<Partner> getActivePartners() {
		return partnerReader.readAllActive();
	}

	public List<Partner> getActivePartners(ServiceType serviceType) {
		return partnerReader.readAllActiveByServiceType(serviceType);
	}

}