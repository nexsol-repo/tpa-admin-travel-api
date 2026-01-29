package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tpa_partner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPartnerEntity extends BaseEntity {

    private String partnerCode;

    private String partnerName;

    private String businessRegistrationNumber;

    private String ceoName;

    private String address;

    private String serviceType;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isActive;

}
