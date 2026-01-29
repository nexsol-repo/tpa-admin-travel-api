package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.partner.Partner;
import com.nexsol.tpa.core.domain.partner.PartnerRepository;
import com.nexsol.tpa.core.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PartnerRepositoryImpl implements PartnerRepository {

    private final PartnerJpaRepository partnerJpaRepository;

    @Override
    public List<Partner> findAllActive() {
        return partnerJpaRepository.findByIsActiveTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Partner> findAllActiveByServiceType(ServiceType serviceType) {
        return partnerJpaRepository.findAllActiveByServiceType(serviceType.name())
            .stream()
            .map(this::toDomain)
            .toList();
    }

    private Partner toDomain(TravelPartnerEntity entity) {
        return new Partner(entity.getId(), entity.getPartnerCode(), entity.getPartnerName());
    }

}
