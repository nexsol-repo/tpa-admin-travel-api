package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 새로운 계약 생성 개념 객체
 */
@Builder
public record NewContract(ContractStatus status, ContractOrigin origin, PlanSelection plan,
		LocalDateTime applicationDate, ContractPeriod period, String policyNumber, ContractApplicant applicant,
		ContractPayment payment, ContractRefund refund, List<ContractCompanion> companions, String memo,
		Long employeeId) {
}