package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "travel_contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelContractEntity extends BaseEntity {

    private Long partnerId;

    private Long channelId;

    private Long insurerId;

    private Long planId;

    private String policyNumber;

    private String partnerName;

    private String channelName;

    private String insurerName;

    @Column(name = "contract_people_name")
    private String applicantName;

    @Column(name = "contract_people_resident_number")
    private String applicantResidentNumber;

    @Column(name = "contract_people_hp")
    private String applicantPhone;

    @Column(name = "contract_people_mail")
    private String applicantEmail;

    private String countryName;

    private String status;

    private LocalDateTime applyDate;

    private LocalDateTime insureStartDate;

    private LocalDateTime insureEndDate;

    private BigDecimal totalPremium;

}
