package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 계약 수정 개념 객체
 */
@Builder
public record ModifyContract(Long contractId, ContractStatus status, String statusName, ContractOrigin origin,
		PlanSelection plan, String policyNumber, String policyLink, BigDecimal totalPremium,
		LocalDateTime applicationDate, ContractPeriod period, ContractApplicant applicant, ContractPayment payment,
		ContractRefund refund, List<ModifyInsuredPerson> insuredPeople, String memo, Long employeeId) {
}