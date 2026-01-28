package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 계약 생성 명령 객체 Presentation Layer에서 Business Layer로 전달되는 개념 객체
 */
@Builder
public record ContractCreateCommand(ContractStatus status, SubscriptionOriginCommand subscriptionOrigin, Long planId,
        String travelCountry, LocalDateTime applicationDate, PeriodCommand period, String policyNumber,
        ApplicantCommand applicant, PaymentCommand payment, List<CompanionCommand> companions, String memo) {

    /**
     * 가입 출처 정보 (제휴사, 채널, 보험사)
     */
    @Builder
    public record SubscriptionOriginCommand(Long partnerId, String partnerName, Long channelId, String channelName,
            Long insurerId, String insurerName) {
    }

    /**
     * 보험 기간 정보
     */
    @Builder
    public record PeriodCommand(LocalDateTime startDate, LocalDateTime endDate) {
    }

    /**
     * 가입자(피보험자) 정보 - 대표 계약자
     */
    @Builder
    public record ApplicantCommand(String name, String residentNumber, String phoneNumber, String email) {
    }

    /**
     * 결제 정보
     */
    @Builder
    public record PaymentCommand(String method, BigDecimal totalAmount, LocalDateTime paidAt,
            LocalDateTime canceledAt) {
    }

    /**
     * 동반자 정보
     */
    @Builder
    public record CompanionCommand(String residentNumber, String gender, String name, String englishName,
            String englishLastName, String passportNumber, String policyNumber, BigDecimal premium) {
    }

}
