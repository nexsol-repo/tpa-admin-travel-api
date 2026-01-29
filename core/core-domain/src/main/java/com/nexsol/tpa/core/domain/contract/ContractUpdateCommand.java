package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 계약 수정 명령 객체 Presentation Layer에서 Business Layer로 전달되는 개념 객체
 */
@Builder
public record ContractUpdateCommand(Long contractId, ContractStatus status, ApplicantUpdateCommand applicant,
        PeriodUpdateCommand period, List<InsuredPersonUpdateCommand> insuredPeople, PaymentUpdateCommand payment,
        SubscriptionOriginUpdateCommand subscriptionOrigin, Long planId, LocalDateTime applicationDate, String memo,
        Long employeeId) {

    /**
     * 가입 출처 정보 수정 명령 (보험사, 채널, 제휴사 - id와 name 필요)
     */
    @Builder
    public record SubscriptionOriginUpdateCommand(Long insurerId, String insurerName, Long channelId,
            String channelName, Long partnerId, String partnerName) {
    }

    @Builder
    public record ApplicantUpdateCommand(String name, String phoneNumber, String email) {
    }

    @Builder
    public record PeriodUpdateCommand(LocalDateTime startDate, LocalDateTime endDate) {
    }

    @Builder
    public record InsuredPersonUpdateCommand(String name, String englishName, String residentNumber,
            String passportNumber, String policyNumber, String gender, BigDecimal premium) {
    }

    @Builder
    public record PaymentUpdateCommand(String method, LocalDateTime paidAt, LocalDateTime canceledAt) {
    }
}
