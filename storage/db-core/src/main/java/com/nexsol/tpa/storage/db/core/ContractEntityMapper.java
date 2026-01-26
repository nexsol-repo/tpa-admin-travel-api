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
     * Case 1: 모든 정보가 다 있는 경우 (상세 조회용)
     */
    public InsuranceContract toDomain(TravelContractEntity c, TravelInsurePaymentEntity p,
            List<TravelInsurePeopleEntity> people) {
        // Null Safety: people이 null이어도 빈 리스트로 처리
        List<TravelInsurePeopleEntity> safePeople = (people != null) ? people : Collections.emptyList();

        return InsuranceContract.builder()
            .contractId(c.getId())
            .status(determineStatus(c, p))
            .metaInfo(mapMeta(c))
            .productPlan(mapProductPlan(c))
            .applicant(mapApplicant(c))
            .paymentInfo(mapPayment(p)) // p가 null이면 null 반환
            .insuredPeople(safePeople.stream().map(this::mapPerson).toList())
            .build();
    }

    /**
     * Case 2: 계약 정보만 있는 경우 (목록 조회 등)
     */
    public InsuranceContract toDomain(TravelContractEntity c) {
        return toDomain(c, null, Collections.emptyList());
    }

    private ContractMeta mapMeta(TravelContractEntity c) {
        return ContractMeta.builder()
            .policyNumber(c.getPolicyNumber())
            .origin(SubscriptionOrigin.builder()
                .partnerName(String.valueOf(c.getPartnerId()))
                .channelName(String.valueOf(c.getChannelId()))
                .build())
            .applicationDate(c.getApplyDate())
            .period(new InsurancePeriod(c.getInsureBeginDate(), c.getInsureEndDate()))
            .build();
    }

    private ProductPlan mapProductPlan(TravelContractEntity c) {
        return ProductPlan.builder()
            .productName("해외여행보험") // 필요시 DB 조회
            .planName("알뜰 플랜")
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

    private PaymentInfo mapPayment(TravelInsurePaymentEntity p) {
        if (p == null)
            return null;
        return PaymentInfo.builder()
            .method(p.getPaymentMethod())
            .totalAmount(BigDecimal.valueOf(p.getPaidAmount()))
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
            .individualPremium(BigDecimal.valueOf(p.getFee()))
            .iIndividualPolicyNumber(p.getInsureNumber())
            .build();
    }

    private ContractStatus determineStatus(TravelContractEntity c, TravelInsurePaymentEntity p) {
        if (p != null && "CANCELED".equals(p.getStatus()))
            return ContractStatus.CANCELED;
        return ContractStatus.COMPLETED;
    }

}