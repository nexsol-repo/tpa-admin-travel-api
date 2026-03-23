package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.contract.ContractUpdateCommand;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ContractUpdateRequest(ContractStatus status, String statusName, ApplicantRequest applicant,
		PeriodRequest period, List<InsuredPersonRequest> insuredPeople, PaymentRequest payment, RefundRequest refund,
		SubscriptionOriginRequest subscriptionOrigin, Long planId, String planName, Boolean silsonExclude,
		String travelCountry, String countryCode, String policyNumber, String policyLink, BigDecimal totalPremium,
		@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime applicationDate, String memo) {

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

	public record ApplicantRequest(String name, String residentNumber, String phoneNumber, String email) {
		public ContractUpdateCommand.ApplicantUpdateCommand toCommand() {
			return ContractUpdateCommand.ApplicantUpdateCommand.builder()
				.name(name)
				.residentNumber(residentNumber)
				.phoneNumber(phoneNumber)
				.email(email)
				.build();
		}
	}

	public record PeriodRequest(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		public ContractUpdateCommand.PeriodUpdateCommand toCommand() {
			return ContractUpdateCommand.PeriodUpdateCommand.builder().startDate(startDate).endDate(endDate).build();
		}
	}

	public record InsuredPersonRequest(Long id, String name, String englishName, String residentNumber,
			String passportNumber, String gender, String policyNumber, BigDecimal premium) {
		public ContractUpdateCommand.InsuredPersonUpdateCommand toCommand() {
			return ContractUpdateCommand.InsuredPersonUpdateCommand.builder()
				.id(id)
				.name(name)
				.englishName(englishName)
				.residentNumber(residentNumber)
				.passportNumber(passportNumber)
				.gender(gender)
				.policyNumber(policyNumber)
				.premium(premium)
				.build();
		}
	}

	public record PaymentRequest(String status, String method, BigDecimal totalAmount,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime paidAt,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime canceledAt) {
		public ContractUpdateCommand.PaymentUpdateCommand toCommand() {
			return ContractUpdateCommand.PaymentUpdateCommand.builder()
				.status(status)
				.method(method)
				.totalAmount(totalAmount)
				.paidAt(paidAt)
				.canceledAt(canceledAt)
				.build();
		}
	}

	public record RefundRequest(BigDecimal refundAmount, String refundMethod, String bankName, String accountNumber,
			String depositorName, String refundReason,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime refundedAt) {
		public ContractUpdateCommand.RefundUpdateCommand toCommand() {
			return ContractUpdateCommand.RefundUpdateCommand.builder()
				.refundAmount(refundAmount)
				.refundMethod(refundMethod)
				.bankName(bankName)
				.accountNumber(accountNumber)
				.depositorName(depositorName)
				.refundReason(refundReason)
				.refundedAt(refundedAt)
				.build();
		}
	}

	public ContractUpdateCommand toCommand(Long contractId, Long employeeId) {
		return ContractUpdateCommand.builder()
			.contractId(contractId)
			.status(status)
			.statusName(statusName)
			.applicant(applicant != null ? applicant.toCommand() : null)
			.period(period != null ? period.toCommand() : null)
			.insuredPeople(
					insuredPeople != null ? insuredPeople.stream().map(InsuredPersonRequest::toCommand).toList() : null)
			.payment(payment != null ? payment.toCommand() : null)
			.refund(refund != null ? refund.toCommand() : null)
			.subscriptionOrigin(subscriptionOrigin != null ? subscriptionOrigin.toCommand() : null)
			.planId(planId)
			.planName(planName)
			.silsonExclude(silsonExclude)
			.travelCountry(travelCountry)
			.countryCode(countryCode)
			.policyNumber(policyNumber)
			.policyLink(policyLink)
			.totalPremium(totalPremium)
			.applicationDate(applicationDate)
			.memo(memo)
			.employeeId(employeeId)
			.build();
	}
}
