package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.ContractStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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

    @Column(name = "insured_people_number")
    private Integer insuredPeopleNumber;

    public void updateStatus(String status) {
        if (status != null) {
            this.status = status;
        }
    }

    public void updateApplicant(String name, String phone, String email) {
        if (name != null) {
            this.applicantName = name;
        }
        if (phone != null) {
            this.applicantPhone = phone;
        }
        if (email != null) {
            this.applicantEmail = email;
        }
    }

    public void updateInsuredCount(int count) {
        this.insuredPeopleNumber = count;
    }

    public void updateInsurancePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null) {
            this.insureStartDate = startDate;
        }
        if (endDate != null) {
            this.insureEndDate = endDate;
        }
    }

    public InsuranceContract toDomain(TravelInsurePaymentEntity payment,
            List<TravelInsurePeopleEntity> people,
            TravelInsurancePlanEntity plan) {

        List<TravelInsurePeopleEntity> safePeople = (people != null) ? people : Collections.emptyList();

        return InsuranceContract.builder()
            .contractId(this.getId())
            .status(determineStatus(payment))
            .metaInfo(toContractMeta())
            .productPlan(toProductPlan(plan))
            .applicant(toApplicant())
            .paymentInfo(toPaymentInfo(payment))
            .insuredPeople(safePeople.stream().map(TravelInsurePeopleEntity::toDomain).toList())
            .build();
    }

    public InsuranceContract toDomain() {
        return toDomain(null, Collections.emptyList(), null);
    }

    private ContractMeta toContractMeta() {
        return ContractMeta.builder()
            .policyNumber(this.policyNumber)
            .origin(SubscriptionOrigin.builder()
                .partnerName(this.partnerName)
                .channelName(this.channelName)
                .insurerName(this.insurerName)
                .build())
            .applicationDate(this.applyDate)
            .period(new InsurancePeriod(this.insureStartDate, this.insureEndDate))
            .build();
    }

    private ProductPlan toProductPlan(TravelInsurancePlanEntity plan) {
        if (plan == null) {
            return ProductPlan.builder()
                .productName("해외여행보험")
                .planName("알뜰 플랜")
                .travelCountry(this.countryName)
                .build();
        }
        return ProductPlan.builder()
            .productName(plan.getPlanFullName())
            .planName(plan.getPlanName())
            .travelCountry(this.countryName)
            .build();
    }

    private Applicant toApplicant() {
        return Applicant.builder()
            .name(this.applicantName)
            .residentNumber(this.applicantResidentNumber)
            .phoneNumber(this.applicantPhone)
            .email(this.applicantEmail)
            .build();
    }

    private PaymentInfo toPaymentInfo(TravelInsurePaymentEntity payment) {
        if (payment == null) {
            return PaymentInfo.builder()
                .totalAmount(this.totalPremium != null ? this.totalPremium : BigDecimal.ZERO)
                .build();
        }
        return payment.toDomain(this.totalPremium);
    }

    private ContractStatus determineStatus(TravelInsurePaymentEntity payment) {
        if (this.status != null) {
            try {
                return ContractStatus.valueOf(this.status);
            }
            catch (IllegalArgumentException e) {
                // Fallback
            }
        }
        if (payment != null && "CANCELED".equals(payment.getStatus())) {
            return ContractStatus.CANCELED;
        }
        return ContractStatus.COMPLETED;
    }

}
