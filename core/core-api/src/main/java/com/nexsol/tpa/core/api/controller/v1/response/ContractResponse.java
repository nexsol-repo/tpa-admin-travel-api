package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.InsuranceContract;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ContractResponse(Long contractId, String contractStatus, String contractStatusCode, String policyNumber,
        String partnerName, String channelName, String applicantName, String applicantPhone, int insuredCount,
        BigDecimal totalPremium, LocalDateTime applicationDate, LocalDateTime insuranceStartDate,
        LocalDateTime insuranceEndDate) {
    public static ContractResponse of(InsuranceContract domain) {
        return ContractResponse.builder()
            .contractId(domain.contractId())
            .contractStatus(domain.status().name())
            .contractStatusCode(domain.status().name())
            .policyNumber(domain.metaInfo().policyNumber())
            .partnerName(domain.metaInfo().partnerName())
            .channelName(domain.metaInfo().channelName())
            .applicantName(domain.applicant().name())
            .applicantPhone(domain.applicant().phoneNumber())
            .insuredCount(domain.insuredPeople().size())
            .totalPremium(domain.paymentInfo() != null ? domain.paymentInfo().totalAmount() : BigDecimal.ZERO)
            .applicationDate(domain.metaInfo().applicationDate())
            .insuranceStartDate(domain.metaInfo().period().startDate())
            .insuranceEndDate(domain.metaInfo().period().endDate())
            .build();
    }
}