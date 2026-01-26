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
            List<TravelInsurePeopleEntity> people, String partnerName, String channelName, String insurerName) {
        // Null Safety
        List<TravelInsurePeopleEntity> safePeople = (people != null) ? people : Collections.emptyList();

        return InsuranceContract.builder()
            .contractId(c.getId())
            .status(determineStatus(c, p))
            .metaInfo(mapMeta(c, partnerName, channelName, insurerName)) // [수정] 이름 전달
            .productPlan(mapProductPlan(c))
            .applicant(mapApplicant(c))
            .paymentInfo(mapPayment(p, c))
            .insuredPeople(safePeople.stream().map(this::mapPerson).toList())
            .build();
    }

    /**
     * Case 2: 계약 정보만 있는 경우 (목록 조회 등 - 이름 정보가 없을 때 ID로 대체)
     */
    public InsuranceContract toDomain(TravelContractEntity c) {
        // 이름 정보가 없으면 ID를 문자로 변환하여 전달
        return toDomain(c, null, Collections.emptyList(), String.valueOf(c.getPartnerId()),
                String.valueOf(c.getChannelId()), String.valueOf(c.getInsurerId()));
    }

    // [수정] 이름 정보를 파라미터로 받도록 변경
    private ContractMeta mapMeta(TravelContractEntity c, String partnerName, String channelName, String insurerName) {
        return ContractMeta.builder()
            .policyNumber(c.getPolicyNumber())
            .origin(new SubscriptionOrigin(partnerName, channelName, insurerName)) // [수정]
                                                                                   // 전달받은
                                                                                   // 이름
                                                                                   // 사용
            .applicationDate(c.getApplyDate())
            .period(new InsurancePeriod(c.getInsureBeginDate(), c.getInsureEndDate()))
            .build();
    }

    private ProductPlan mapProductPlan(TravelContractEntity c) {
        return ProductPlan.builder()
            .productName("해외여행보험")
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

    // [보강] Payment가 null이어도 Contract의 total_fee를 사용할 수 있도록 수정
    private PaymentInfo mapPayment(TravelInsurePaymentEntity p, TravelContractEntity c) {
        if (p == null) {
            // 결제 정보는 없지만 계약에 총액이 있는 경우 처리
            return PaymentInfo.builder()
                .totalAmount(c.getTotalPremium() != null ? c.getTotalPremium() : BigDecimal.ZERO)
                .build();
        }
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