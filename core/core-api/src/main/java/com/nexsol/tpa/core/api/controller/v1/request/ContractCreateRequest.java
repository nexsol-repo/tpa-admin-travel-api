package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.contract.ContractCreateCommand;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 보험 가입 직접등록 요청 DTO
 */
public record ContractCreateRequest(
		// 보험 가입 정보
		ContractStatus status, SubscriptionOriginRequest subscriptionOrigin, Long planId, String planName,
		Boolean silsonExclude, String travelCountry, String countryCode,
		@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime applicationDate, PeriodRequest period,
		String policyNumber,

		// 가입자(피보험자) 정보
		ApplicantRequest applicant,

		// 결제 정보
		PaymentRequest payment,

		// 동반자 정보
		List<CompanionRequest> companions,

		// 메모
		String memo) {

	/**
	 * 가입 출처 정보 (제휴사, 채널, 보험사)
	 */
	public record SubscriptionOriginRequest(Long partnerId, String partnerName, Long channelId, String channelName,
			Long insurerId, String insurerName) {
		public ContractCreateCommand.SubscriptionOriginCommand toCommand() {
			return ContractCreateCommand.SubscriptionOriginCommand.builder()
				.partnerId(partnerId)
				.partnerName(partnerName)
				.channelId(channelId)
				.channelName(channelName)
				.insurerId(insurerId)
				.insurerName(insurerName)
				.build();
		}
	}

	/**
	 * 보험 기간 정보 (출발일/시간, 도착일/시간)
	 */
	public record PeriodRequest(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		public ContractCreateCommand.PeriodCommand toCommand() {
			return ContractCreateCommand.PeriodCommand.builder().startDate(startDate).endDate(endDate).build();
		}
	}

	/**
	 * 가입자(피보험자) 정보 - 대표 계약자
	 */
	public record ApplicantRequest(String name, String residentNumber, String phoneNumber, String email) {
		public ContractCreateCommand.ApplicantCommand toCommand() {
			return ContractCreateCommand.ApplicantCommand.builder()
				.name(name)
				.residentNumber(residentNumber)
				.phoneNumber(phoneNumber)
				.email(email)
				.build();
		}
	}

	/**
	 * 결제 정보
	 */
	public record PaymentRequest(String method, BigDecimal totalAmount,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime paidAt,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime canceledAt) {
		public ContractCreateCommand.PaymentCommand toCommand() {
			return ContractCreateCommand.PaymentCommand.builder()
				.method(method)
				.totalAmount(totalAmount)
				.paidAt(paidAt)
				.canceledAt(canceledAt)
				.build();
		}
	}

	/**
	 * 동반자 정보
	 */
	public record CompanionRequest(String residentNumber, String gender, String name, String englishName,
			String englishLastName, String passportNumber, String policyNumber, BigDecimal premium) {
		public ContractCreateCommand.CompanionCommand toCommand() {
			return ContractCreateCommand.CompanionCommand.builder()
				.residentNumber(residentNumber)
				.gender(gender)
				.name(name)
				.englishName(englishName)
				.englishLastName(englishLastName)
				.passportNumber(passportNumber)
				.policyNumber(policyNumber)
				.premium(premium)
				.build();
		}
	}

	public ContractCreateCommand toCommand(Long employeeId) {
		return ContractCreateCommand.builder()
			.status(status)
			.subscriptionOrigin(subscriptionOrigin != null ? subscriptionOrigin.toCommand() : null)
			.planId(planId)
			.planName(planName)
			.silsonExclude(silsonExclude)
			.travelCountry(travelCountry)
			.countryCode(countryCode)
			.applicationDate(applicationDate)
			.period(period != null ? period.toCommand() : null)
			.policyNumber(policyNumber)
			.applicant(applicant != null ? applicant.toCommand() : null)
			.payment(payment != null ? payment.toCommand() : null)
			.companions(companions != null ? companions.stream().map(CompanionRequest::toCommand).toList() : null)
			.memo(memo)
			.employeeId(employeeId)
			.build();
	}

}
