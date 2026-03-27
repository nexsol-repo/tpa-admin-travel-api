package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.contract.*;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 보험 가입 직접등록 요청 DTO
 */
public record ContractCreateRequest(ContractStatus status, SubscriptionOriginRequest subscriptionOrigin, Long planId,
		String planName, Boolean silsonExclude, String travelCountry, String countryCode,
		@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime applicationDate, PeriodRequest period,
		String policyNumber, ApplicantRequest applicant, PaymentRequest payment, RefundRequest refund,
		List<CompanionRequest> companions, String memo) {

	public record SubscriptionOriginRequest(Long partnerId, String partnerName, Long channelId, String channelName,
			Long insurerId, String insurerName) {
		public ContractOrigin toOrigin() {
			return ContractOrigin.builder()
				.partnerId(partnerId)
				.partnerName(partnerName)
				.channelId(channelId)
				.channelName(channelName)
				.insurerId(insurerId)
				.insurerName(insurerName)
				.build();
		}
	}

	public record PeriodRequest(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		public ContractPeriod toPeriod() {
			return ContractPeriod.builder().startDate(startDate).endDate(endDate).build();
		}
	}

	public record ApplicantRequest(String name, String residentNumber, String phoneNumber, String email) {
		public ContractApplicant toApplicant() {
			return ContractApplicant.builder()
				.name(name)
				.residentNumber(residentNumber)
				.phoneNumber(phoneNumber)
				.email(email)
				.build();
		}
	}

	public record PaymentRequest(String method, BigDecimal totalAmount,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime paidAt,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime canceledAt) {
		public ContractPayment toPayment() {
			return ContractPayment.builder()
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
		public ContractRefund toRefund() {
			return ContractRefund.builder()
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

	public record CompanionRequest(String residentNumber, String gender, String name, String englishName,
			String englishLastName, String passportNumber, String policyNumber, BigDecimal premium) {
		public ContractCompanion toCompanion() {
			return ContractCompanion.builder()
				.residentNumber(residentNumber)
				.gender(gender)
				.name(name)
				.englishName(englishName)
				.englishLastName(englishLastName)
				.passportNumber(passportNumber)
				.premium(premium)
				.build();
		}
	}

	public NewContract toNewContract(Long employeeId) {
		return NewContract.builder()
			.status(status)
			.origin(subscriptionOrigin != null ? subscriptionOrigin.toOrigin() : null)
			.plan(PlanSelection.builder()
				.planId(planId)
				.planName(planName)
				.silsonExclude(silsonExclude)
				.travelCountry(travelCountry)
				.countryCode(countryCode)
				.build())
			.applicationDate(applicationDate)
			.period(period != null ? period.toPeriod() : null)
			.policyNumber(policyNumber)
			.applicant(applicant != null ? applicant.toApplicant() : null)
			.payment(payment != null ? payment.toPayment() : null)
			.refund(refund != null ? refund.toRefund() : null)
			.companions(companions != null ? companions.stream().map(CompanionRequest::toCompanion).toList() : null)
			.memo(memo)
			.employeeId(employeeId)
			.build();
	}

}