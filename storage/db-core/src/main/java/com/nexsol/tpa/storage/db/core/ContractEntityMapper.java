package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.*;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class ContractEntityMapper {

    /**
     * Case 1: 모든 정보가 다 있는 경우 (상세 조회용) [Refactored] Name 파라미터 제거 -> Entity 내부 필드 사용
     */
    public InsuranceContract toDomain(TravelContractEntity c, TravelInsurePaymentEntity p,
            List<TravelInsurePeopleEntity> people) {
        // Null Safety
        List<TravelInsurePeopleEntity> safePeople = (people != null) ? people : Collections.emptyList();

        return InsuranceContract.builder()
            .contractId(c.getId())
            .status(determineStatus(c, p))
            .metaInfo(mapMeta(c))
            .productPlan(mapProductPlan(c))
            .applicant(mapApplicant(c))
            .paymentInfo(mapPayment(p, c))
            .insuredPeople(safePeople.stream().map(this::mapPerson).toList())
            .build();
    }

    /**
     * Case 2: 계약 정보만 있는 경우 (목록 조회 등) [Refactored] Name 파라미터 제거
     */
    public InsuranceContract toDomain(TravelContractEntity c) {
        return toDomain(c, null, Collections.emptyList());
    }

    // [Refactored] Entity의 Name 필드 사용
    private ContractMeta mapMeta(TravelContractEntity c) {
        return ContractMeta.builder()
            .policyNumber(c.getPolicyNumber())
            .origin(SubscriptionOrigin.builder()
                .partnerName(c.getPartnerName())
                .channelName(c.getChannelName())
                .insurerName(c.getInsurerName())
                .build())
            .applicationDate(c.getApplyDate())
            .period(new InsurancePeriod(c.getInsureStartDate(), c.getInsureEndDate()))
            .build();
    }

    private ProductPlan mapProductPlan(TravelContractEntity c) {
        return ProductPlan.builder()
            .productName("해외여행보험")
            .planName("알뜰 플랜") // TODO: Plan Entity 조인 혹은 역정규화 필요 시 추후 개선
            .travelCountry(c.getCountryName())
            .coverageLink(null)
            .build();
    }

    private Applicant mapApplicant(TravelContractEntity c) {
        return Applicant.builder()
            .name(c.getApplicantName())
            .residentNumber(c.getApplicantResidentNumber())
            .phoneNumber(c.getApplicantPhone())
            .email(c.getApplicantEmail())
            .build();
    }

    private PaymentInfo mapPayment(TravelInsurePaymentEntity p, TravelContractEntity c) {
        if (p == null) {
            // 결제 정보는 없지만 계약에 총액이 있는 경우 처리
            return PaymentInfo.builder()
                .totalAmount(c.getTotalPremium() != null ? c.getTotalPremium() : BigDecimal.ZERO)
                .build();
        }
        return PaymentInfo.builder()
            .method(p.getPaymentMethod())
            .totalAmount(p.getPaidAmount())
            .paidAt(p.getPaymentDate())
            .canceledAt(p.getCancelDate())
            .build();
    }

    private InsuredPerson mapPerson(TravelInsurePeopleEntity p) {
        return InsuredPerson.builder()
            .name(p.getName())
            .englishName(p.getEnglishName())
            .residentNumber(p.getResidentNumber())
            .passportNumber(p.getPassportNumber())
            .gender(p.getGender())
            .individualPremium(p.getInsurePremium())
            .iIndividualPolicyNumber(p.getInsureNumber())
            .build();
    }

    private ContractStatus determineStatus(TravelContractEntity c, TravelInsurePaymentEntity p) {
        // Entity status 우선 고려 가능 (c.getStatus())
        if (c.getStatus() != null) {
            try {
                return ContractStatus.valueOf(c.getStatus());
            }
            catch (IllegalArgumentException e) {
                // Fallback
            }
        }

        if (p != null && "CANCELED".equals(p.getStatus()))
            return ContractStatus.CANCELED;
        return ContractStatus.COMPLETED;
    }

}