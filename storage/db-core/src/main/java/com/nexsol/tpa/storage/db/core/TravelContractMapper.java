package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.contract.ContractMeta;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class TravelContractMapper {

    /**
     * 도메인 모델 -> 엔티티 변환 (생성 시 사용)
     */
    public TravelContractEntity toEntity(InsuranceContract contract) {
        if (contract == null) {
            return null;
        }

        TravelContractEntity entity = new TravelContractEntity();

        // 상태
        if (contract.status() != null) {
            entity.updateStatus(contract.status().name());
        }

        // 가입 출처 및 기간 정보
        if (contract.metaInfo() != null) {
            mapMetaInfo(entity, contract.metaInfo());
        }

        // 가입자 정보
        entity.updateApplicant(contract.applicant());

        // 플랜 정보
        if (contract.productPlan() != null) {
            entity.updatePlanId(contract.productPlan().planId());
            entity.updateCountryName(contract.productPlan().travelCountry());
        }

        // 결제 정보
        if (contract.paymentInfo() != null) {
            entity.updateTotalPremium(contract.paymentInfo().totalAmount());
        }

        // 피보험자 수
        entity.updateInsuredCount(contract.getTotalInsuredCount());

        // 담당자 ID
        entity.updateEmployeeId(contract.employeeId());

        return entity;
    }

    /**
     * 엔티티 -> 도메인 모델 변환
     */
    public InsuranceContract toDomain(TravelContractEntity entity, TravelInsurePaymentEntity payment,
            List<TravelInsurePeopleEntity> people, TravelInsurancePlanEntity plan) {
        if (entity == null) {
            return null;
        }

        List<TravelInsurePeopleEntity> safePeople = (people != null) ? people : Collections.emptyList();

        return InsuranceContract.builder()
            .contractId(entity.getId())
            .status(determineStatus(entity, payment))
            .metaInfo(toContractMeta(entity))
            .productPlan(toProductPlan(entity, plan))
            .applicant(toApplicant(entity))
            .paymentInfo(toPaymentInfo(entity, payment))
            .insuredPeople(safePeople.stream().map(TravelInsurePeopleEntity::toDomain).toList())
            .employeeId(entity.getEmployeeId())
            .build();
    }

    public InsuranceContract toDomain(TravelContractEntity entity) {
        return toDomain(entity, null, Collections.emptyList(), null);
    }

    private void mapMetaInfo(TravelContractEntity entity, ContractMeta meta) {
        entity.updateSubscriptionOrigin(meta.origin());
        entity.updateInsurancePeriod(meta.period());
        if (meta.policyNumber() != null) {
            entity.updatePolicyNumber(meta.policyNumber());
        }
        if (meta.applicationDate() != null) {
            entity.updateApplyDate(meta.applicationDate());
        }
    }

    private ContractMeta toContractMeta(TravelContractEntity entity) {
        return ContractMeta.builder()
            .policyNumber(entity.getPolicyNumber())
            .origin(SubscriptionOrigin.builder()
                .partnerId(entity.getPartnerId())
                .partnerName(entity.getPartnerName())
                .channelId(entity.getChannelId())
                .channelName(entity.getChannelName())
                .insurerId(entity.getInsurerId())
                .insurerName(entity.getInsurerName())
                .build())
            .applicationDate(entity.getApplyDate())
            .period(new InsurancePeriod(entity.getInsureStartDate(), entity.getInsureEndDate()))
            .build();
    }

    private ProductPlan toProductPlan(TravelContractEntity entity, TravelInsurancePlanEntity plan) {
        if (plan == null) {
            return ProductPlan.builder()
                .planId(entity.getPlanId())
                .productName(plan.getProductName())
                .planName(plan.getPlanName())
                .travelCountry(entity.getCountryName())
                .build();
        }
        return ProductPlan.builder()
            .planId(plan.getId())
            .productName(plan.getProductName())
            .planName(plan.getPlanName())
            .travelCountry(entity.getCountryName())
            .build();
    }

    private Applicant toApplicant(TravelContractEntity entity) {
        return Applicant.builder()
            .name(entity.getApplicantName())
            .residentNumber(entity.getApplicantResidentNumber())
            .phoneNumber(entity.getApplicantPhone())
            .email(entity.getApplicantEmail())
            .build();
    }

    private PaymentInfo toPaymentInfo(TravelContractEntity entity, TravelInsurePaymentEntity payment) {
        if (payment == null) {
            return PaymentInfo.builder()
                .totalAmount(entity.getTotalPremium() != null ? entity.getTotalPremium() : BigDecimal.ZERO)
                .build();
        }
        return payment.toDomain(entity.getTotalPremium());
    }

    private ContractStatus determineStatus(TravelContractEntity entity, TravelInsurePaymentEntity payment) {
        if (entity.getStatus() != null) {
            try {
                return ContractStatus.valueOf(entity.getStatus());
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
