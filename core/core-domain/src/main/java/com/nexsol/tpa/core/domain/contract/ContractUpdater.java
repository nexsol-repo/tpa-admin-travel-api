package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.applicant.InsuredPeopleUpdater;
import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.payment.RefundInfo;
import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.plan.Plan;
import com.nexsol.tpa.core.domain.plan.PlanReader;
import com.nexsol.tpa.core.domain.plan.PlanResolver;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
import com.nexsol.tpa.core.enums.ContractStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 계약 수정 도구 클래스 (Implement Layer) 수정 로직의 상세 구현을 담당
 */
@Component
@RequiredArgsConstructor
public class ContractUpdater {

	private final ContractRepository contractRepository;

	private final PlanReader planReader;

	private final PlanResolver planResolver;

	private final InsuredPeopleUpdater insuredPeopleUpdater;

	public Long update(ContractUpdateCommand command) {
		InsuranceContract existing = contractRepository.findById(command.contractId())
			.orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));

		InsuranceContract updated = applyChanges(existing, command);

		Long contractId = contractRepository.save(updated);

		// 동반자 수정은 별도 도구 클래스에 위임
		insuredPeopleUpdater.update(contractId, updated.insuredPeople());

		return contractId;
	}

	private InsuranceContract applyChanges(InsuranceContract existing, ContractUpdateCommand command) {
		return InsuranceContract.builder()
			.contractId(existing.contractId())
			.status(resolveStatus(existing, command))
			.metaInfo(updateMeta(existing.metaInfo(), command))
			.productPlan(updateProductPlan(existing, command))
			.applicant(updateApplicant(existing.applicant(), command.applicant()))
			.paymentInfo(updatePayment(existing.paymentInfo(), command.payment(), command.statusName()))
			.refundInfo(updateRefund(existing.refundInfo(), command.refund()))
			.insuredPeople(updateInsuredPeople(existing.insuredPeople(), command.insuredPeople()))
			.employeeId(command.employeeId() != null ? command.employeeId() : existing.employeeId())
			.build();
	}

	private ContractStatus resolveStatus(InsuranceContract existing, ContractUpdateCommand command) {
		return command.status() != null ? command.status() : existing.status();
	}

	private PaymentInfo updatePayment(PaymentInfo existing, ContractUpdateCommand.PaymentUpdateCommand command,
			String statusName) {
		if (command == null && statusName == null) {
			return existing;
		}

		String currentStatus = (existing != null) ? existing.status() : null;
		String currentMethod = (existing != null) ? existing.method() : null;
		BigDecimal currentAmount = (existing != null) ? existing.totalAmount() : BigDecimal.ZERO;
		LocalDateTime currentPaidAt = (existing != null) ? existing.paidAt() : null;
		LocalDateTime currentCanceledAt = (existing != null) ? existing.canceledAt() : null;

		// 1. payment.status 직접 수정이 우선
		String resolvedStatus = currentStatus;
		if (command != null && command.status() != null) {
			resolvedStatus = command.status();
		}
		// 2. statusName(가입완료/임의해지)에 따라 payment.status 연동
		else if (statusName != null) {
			resolvedStatus = resolvePaymentStatusByName(statusName, currentStatus);
		}

		return PaymentInfo.builder()
			.status(resolvedStatus)
			.method(command != null && command.method() != null ? command.method() : currentMethod)
			.totalAmount(currentAmount)
			.paidAt(command != null && command.paidAt() != null ? command.paidAt() : currentPaidAt)
			.canceledAt(command != null ? command.canceledAt() : currentCanceledAt)
			.build();
	}

	private String resolvePaymentStatusByName(String statusName, String currentStatus) {
		return switch (statusName) {
			case "임의해지" -> "CANCELED";
			case "가입완료" -> "COMPLETED";
			default -> currentStatus;
		};
	}

	private ContractMeta updateMeta(ContractMeta existing, ContractUpdateCommand command) {
		InsurancePeriod updatedPeriod = existing.period();
		if (command.period() != null) {
			updatedPeriod = InsurancePeriod.builder()
				.startDate(command.period().startDate() != null ? command.period().startDate()
						: existing.period().startDate())
				.endDate(command.period().endDate() != null ? command.period().endDate() : existing.period().endDate())
				.build();
		}

		SubscriptionOrigin updatedOrigin = updateSubscriptionOrigin(existing.origin(), command.subscriptionOrigin());

		return ContractMeta.builder()
			.policyNumber(command.policyNumber() != null ? command.policyNumber() : existing.policyNumber())
			.policyLink(command.policyLink() != null ? command.policyLink() : existing.policyLink())
			.origin(updatedOrigin)
			.applicationDate(command.applicationDate() != null ? command.applicationDate() : existing.applicationDate())
			.period(updatedPeriod)
			.build();
	}

	/**
	 * 가입 출처 정보 수정 (보험사, 채널, 제휴사 - id와 name 함께 수정)
	 */
	private SubscriptionOrigin updateSubscriptionOrigin(SubscriptionOrigin existing,
			ContractUpdateCommand.SubscriptionOriginUpdateCommand command) {
		if (command == null) {
			return existing;
		}

		return SubscriptionOrigin.builder()
			.insurerId(command.insurerId() != null ? command.insurerId() : existing.insurerId())
			.insurerName(command.insurerName() != null ? command.insurerName() : existing.insurerName())
			.insurerCode(existing.insurerCode())
			.channelId(command.channelId() != null ? command.channelId() : existing.channelId())
			.channelName(command.channelName() != null ? command.channelName() : existing.channelName())
			.channelCode(existing.channelCode())
			.partnerId(command.partnerId() != null ? command.partnerId() : existing.partnerId())
			.partnerName(command.partnerName() != null ? command.partnerName() : existing.partnerName())
			.partnerCode(existing.partnerCode())
			.build();
	}

	/**
	 * 플랜 정보 수정 - planName이 있으면 가입자 주민번호로 플랜 resolve, 없으면 planId로 직접 조회
	 */
	private ProductPlan updateProductPlan(InsuranceContract existing, ContractUpdateCommand command) {
		ProductPlan existingPlan = existing.productPlan();
		Long planId = command.planId();
		String travelCountry = command.travelCountry();
		String countryCode = command.countryCode();

		if (planId == null && command.planName() == null && travelCountry == null && countryCode == null) {
			return existingPlan;
		}

		String productName = existingPlan.productName();
		String planName = existingPlan.planName();
		Long resolvedPlanId = existingPlan.planId();

		// planName이 있으면 가입자 주민번호로 플랜 resolve
		if (command.planName() != null && command.applicant() != null && command.applicant().residentNumber() != null) {
			Plan plan = planResolver.resolve(command.planName(), command.applicant().residentNumber(),
					command.silsonExclude());
			resolvedPlanId = plan.id();
			productName = plan.fullName();
			planName = plan.name();
		}
		else if (command.planName() != null && existing.applicant() != null
				&& existing.applicant().residentNumber() != null) {
			Plan plan = planResolver.resolve(command.planName(), existing.applicant().residentNumber(),
					command.silsonExclude());
			resolvedPlanId = plan.id();
			productName = plan.fullName();
			planName = plan.name();
		}
		else if (planId != null) {
			Plan plan = planReader.read(planId).orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));
			resolvedPlanId = plan.id();
			productName = plan.fullName();
			planName = plan.name();
		}

		return ProductPlan.builder()
			.planId(resolvedPlanId)
			.productName(productName)
			.planName(planName)
			.travelCountry(travelCountry != null ? travelCountry : existingPlan.travelCountry())
			.countryCode(countryCode != null ? countryCode : existingPlan.countryCode())
			.coverageLink(existingPlan.coverageLink())
			.build();
	}

	private RefundInfo updateRefund(RefundInfo existing, ContractUpdateCommand.RefundUpdateCommand command) {
		if (command == null) {
			return existing;
		}
		return RefundInfo.builder()
			.refundAmount(command.refundAmount() != null ? command.refundAmount()
					: (existing != null ? existing.refundAmount() : null))
			.refundMethod(command.refundMethod() != null ? command.refundMethod()
					: (existing != null ? existing.refundMethod() : null))
			.bankName(command.bankName())
			.accountNumber(command.accountNumber())
			.depositorName(command.depositorName())
			.refundReason(command.refundReason())
			.refundedAt(command.refundedAt() != null ? command.refundedAt()
					: (existing != null ? existing.refundedAt() : null))
			.build();
	}

	private Applicant updateApplicant(Applicant existing, ContractUpdateCommand.ApplicantUpdateCommand command) {
		if (command == null) {
			return existing;
		}

		return Applicant.builder()
			.name(command.name() != null ? command.name() : existing.name())
			.residentNumber(command.residentNumber() != null ? command.residentNumber() : existing.residentNumber())
			.phoneNumber(command.phoneNumber() != null ? command.phoneNumber() : existing.phoneNumber())
			.email(command.email() != null ? command.email() : existing.email())
			.build();
	}

	private List<InsuredPerson> updateInsuredPeople(List<InsuredPerson> existing,
			List<ContractUpdateCommand.InsuredPersonUpdateCommand> commands) {

		// null이면 기존 유지 (수정 안 함)
		if (commands == null) {
			return existing;
		}

		// 빈 리스트면 빈 리스트 반환 (모든 동반자 삭제)
		if (commands.isEmpty()) {
			return List.of();
		}

		return commands.stream().map(this::toInsuredPerson).toList();
	}

	private InsuredPerson toInsuredPerson(ContractUpdateCommand.InsuredPersonUpdateCommand command) {
		return InsuredPerson.builder()
			.id(command.id())
			.name(command.name())
			.englishName(command.englishName())
			.residentNumber(command.residentNumber())
			.passportNumber(command.passportNumber())
			.individualPolicyNumber(command.policyNumber())
			.gender(command.gender())
			.individualPremium(command.premium())
			.build();
	}

}
