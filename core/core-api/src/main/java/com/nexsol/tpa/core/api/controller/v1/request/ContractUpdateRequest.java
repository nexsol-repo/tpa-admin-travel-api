package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.contract.*;
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

	public record SubscriptionOriginRequest(Long insurerId, String insurerName, Long channelId, String channelName,
			Long partnerId, String partnerName) {
		public ContractOrigin toOrigin() {
			return ContractOrigin.builder()
				.insurerId(insurerId)
				.insurerName(insurerName)
				.channelId(channelId)
				.channelName(channelName)
				.partnerId(partnerId)
				.partnerName(partnerName)
				.build();
		}
	}

	public record ApplicantRequest(String name, String residentNumber, String phoneNumber, String email,
			BigDecimal premium) {
		public ContractApplicant toApplicant() {
			return ContractApplicant.builder()
				.name(name)
				.residentNumber(residentNumber)
				.phoneNumber(phoneNumber)
				.email(email)
				.premium(premium)
				.build();
		}
	}

	public record PeriodRequest(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		public ContractPeriod toPeriod() {
			return ContractPeriod.builder().startDate(startDate).endDate(endDate).build();
		}
	}

	public record InsuredPersonRequest(Long id, String name, String englishName, String residentNumber,
			String passportNumber, String gender, String policyNumber, BigDecimal premium) {
		public ModifyInsuredPerson toModifyInsuredPerson() {
			return ModifyInsuredPerson.builder()
				.id(id)
				.name(name)
				.englishName(englishName)
				.residentNumber(residentNumber)
				.passportNumber(passportNumber)
				.gender(gender)
				.premium(premium)
				.build();
		}
	}

	public record PaymentRequest(String status, String method, BigDecimal totalAmount,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime paidAt,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime canceledAt) {
		public ContractPayment toPayment() {
			return ContractPayment.builder()
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

	public ModifyContract toModifyContract(Long contractId, Long employeeId) {
		return ModifyContract.builder()
			.contractId(contractId)
			.status(status)
			.statusName(statusName)
			.origin(subscriptionOrigin != null ? subscriptionOrigin.toOrigin() : null)
			.plan(PlanSelection.builder()
				.planId(planId)
				.planName(planName)
				.silsonExclude(silsonExclude)
				.travelCountry(travelCountry)
				.countryCode(countryCode)
				.build())
			.policyNumber(policyNumber)
			.policyLink(policyLink)
			.totalPremium(totalPremium)
			.applicationDate(applicationDate)
			.period(period != null ? period.toPeriod() : null)
			.applicant(applicant != null ? applicant.toApplicant() : null)
			.payment(payment != null ? payment.toPayment() : null)
			.refund(refund != null ? refund.toRefund() : null)
			.insuredPeople(insuredPeople != null
					? insuredPeople.stream().map(InsuredPersonRequest::toModifyInsuredPerson).toList() : null)
			.memo(memo)
			.employeeId(employeeId)
			.build();
	}

}