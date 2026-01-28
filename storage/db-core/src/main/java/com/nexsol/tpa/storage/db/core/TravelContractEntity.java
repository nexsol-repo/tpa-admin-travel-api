package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.contract.ContractMeta;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
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




    public static TravelContractEntity create(InsuranceContract contract) {
        TravelContractEntity entity = new TravelContractEntity();

        // 상태
        if (contract.status() != null) {
            entity.status = contract.status().name();
        }

        // 가입 출처 정보
        if (contract.metaInfo() != null) {
            entity.policyNumber = contract.metaInfo().policyNumber();
            entity.applyDate = contract.metaInfo().applicationDate();

            if (contract.metaInfo().origin() != null) {
                SubscriptionOrigin origin = contract.metaInfo().origin();
                entity.partnerId = origin.partnerId();
                entity.partnerName = origin.partnerName();
                entity.channelId = origin.channelId();
                entity.channelName = origin.channelName();
                entity.insurerId = origin.insurerId();
                entity.insurerName = origin.insurerName();
            }

            if (contract.metaInfo().period() != null) {
                entity.insureStartDate = contract.metaInfo().period().startDate();
                entity.insureEndDate = contract.metaInfo().period().endDate();
            }
        }

        // 플랜 정보
        if (contract.productPlan() != null) {
            entity.planId = contract.productPlan().planId();
            entity.countryName = contract.productPlan().travelCountry();
        }

        // 가입자 정보
        if (contract.applicant() != null) {
            entity.applicantName = contract.applicant().name();
            entity.applicantResidentNumber = contract.applicant().residentNumber();
            entity.applicantPhone = contract.applicant().phoneNumber();
            entity.applicantEmail = contract.applicant().email();
        }

        // 결제 정보
        if (contract.paymentInfo() != null) {
            entity.totalPremium = contract.paymentInfo().totalAmount();
        }

        // 피보험자 수
        if (contract.insuredPeople() != null) {
            entity.insuredPeopleNumber = contract.insuredPeople().size();
        }

        return entity;
    }

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

    /**
     * 가입 출처 정보 수정 (보험사, 채널, 제휴사 - id와 name 함께 수정)
     */
    public void updateSubscriptionOrigin(Long insurerId, String insurerName, Long channelId, String channelName,
            Long partnerId, String partnerName) {
        if (insurerId != null) {
            this.insurerId = insurerId;
        }
        if (insurerName != null) {
            this.insurerName = insurerName;
        }
        if (channelId != null) {
            this.channelId = channelId;
        }
        if (channelName != null) {
            this.channelName = channelName;
        }
        if (partnerId != null) {
            this.partnerId = partnerId;
        }
        if (partnerName != null) {
            this.partnerName = partnerName;
        }
    }

    /**
     * 플랜 ID 수정
     */
    public void updatePlanId(Long planId) {
        if (planId != null) {
            this.planId = planId;
        }
    }

    public InsuranceContract toDomain(TravelInsurePaymentEntity payment, List<TravelInsurePeopleEntity> people,
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
                .partnerId(this.partnerId)
                .partnerName(this.partnerName)
                .channelId(this.channelId)
                .channelName(this.channelName)
                .insurerId(this.insurerId)
                .insurerName(this.insurerName)
                .build())
            .applicationDate(this.applyDate)
            .period(new InsurancePeriod(this.insureStartDate, this.insureEndDate))
            .build();
    }

    private ProductPlan toProductPlan(TravelInsurancePlanEntity plan) {
        if (plan == null) {
            return ProductPlan.builder()
                .planId(this.planId)
                .productName("해외여행보험")
                .planName("알뜰 플랜")
                .travelCountry(this.countryName)
                .build();
        }
        return ProductPlan.builder()
            .planId(plan.getId())
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
