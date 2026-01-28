package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.contract.ContractUpdateCommand;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ContractUpdateRequest(ContractStatus status, ApplicantRequest applicant, PeriodRequest period,
        List<InsuredPersonRequest> insuredPeople, PaymentRequest payment, SubscriptionOriginRequest subscriptionOrigin,
        Long planId, String memo) {

    /**
     * 가입 출처 정보 수정 요청 (보험사, 채널, 제휴사 - id와 name 필요)
     */
    public record SubscriptionOriginRequest(Long insurerId, String insurerName, Long channelId, String channelName,
            Long partnerId, String partnerName) {
        public ContractUpdateCommand.SubscriptionOriginUpdateCommand toCommand() {
            return ContractUpdateCommand.SubscriptionOriginUpdateCommand.builder()
                .insurerId(insurerId)
                .insurerName(insurerName)
                .channelId(channelId)
                .channelName(channelName)
                .partnerId(partnerId)
                .partnerName(partnerName)
                .build();
        }
    }

    public record ApplicantRequest(String name, String phoneNumber, String email) {
        public ContractUpdateCommand.ApplicantUpdateCommand toCommand() {
            return ContractUpdateCommand.ApplicantUpdateCommand.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
        }
    }

    public record PeriodRequest(@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate) {
        public ContractUpdateCommand.PeriodUpdateCommand toCommand() {
            return ContractUpdateCommand.PeriodUpdateCommand.builder().startDate(startDate).endDate(endDate).build();
        }
    }

    public record InsuredPersonRequest(String name, String englishName, String residentNumber, String passportNumber,
            String gender) {
        public ContractUpdateCommand.InsuredPersonUpdateCommand toCommand() {
            return ContractUpdateCommand.InsuredPersonUpdateCommand.builder()
                .name(name)
                .englishName(englishName)
                .residentNumber(residentNumber)
                .passportNumber(passportNumber)
                .gender(gender)
                .build();
        }
    }

    public record PaymentRequest(String method, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime paidAt,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime canceledAt) {
        public ContractUpdateCommand.PaymentUpdateCommand toCommand() {
            return ContractUpdateCommand.PaymentUpdateCommand.builder()
                .method(method)
                .paidAt(paidAt)
                .canceledAt(canceledAt)
                .build();
        }
    }

    public ContractUpdateCommand toCommand(Long contractId, Long employeeId) {
        return ContractUpdateCommand.builder()
            .contractId(contractId)
            .status(status)
            .applicant(applicant != null ? applicant.toCommand() : null)
            .period(period != null ? period.toCommand() : null)
            .insuredPeople(
                    insuredPeople != null ? insuredPeople.stream().map(InsuredPersonRequest::toCommand).toList() : null)
            .payment(payment != null ? payment.toCommand() : null)
            .subscriptionOrigin(subscriptionOrigin != null ? subscriptionOrigin.toCommand() : null)
            .planId(planId)
            .memo(memo)
            .employeeId(employeeId)
            .build();
    }
}
