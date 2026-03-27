package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.Applicant;
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
import java.util.Map;
import java.util.Set;

/**
 * 계약 수정 도구 클래스 (Implement Layer)
 */
@Component
@RequiredArgsConstructor
public class ContractUpdater {

	private final PlanReader planReader;

	private final PlanResolver planResolver;

	public InsuranceContract update(InsuranceContract existing, ModifyContract mc) {
		return applyChanges(existing, mc);
	}

	private InsuranceContract applyChanges(InsuranceContract existing, ModifyContract mc) {
		Applicant updatedApplicant = updateApplicant(existing.applicant(), mc.applicant());
		List<InsuredPerson> updatedInsuredPeople = updateInsuredPeople(existing.insuredPeople(), mc.insuredPeople());

		if (mc.applicant() != null) {
			updatedInsuredPeople = syncContractorFromApplicant(updatedInsuredPeople, updatedApplicant);
		}

		return InsuranceContract.builder()
			.contractId(existing.contractId())
			.status(resolveStatus(existing, mc))
			.metaInfo(updateMeta(existing.metaInfo(), mc))
			.productPlan(updateProductPlan(existing, mc))
			.applicant(updatedApplicant)
			.paymentInfo(updatePayment(existing.paymentInfo(), mc.payment(), mc.statusName()))
			.refundInfo(updateRefund(existing.refundInfo(), mc.refund()))
			.insuredPeople(updatedInsuredPeople)
			.employeeId(mc.employeeId() != null ? mc.employeeId() : existing.employeeId())
			.totalPremium(mc.totalPremium() != null ? mc.totalPremium() : existing.totalPremium())
			.build();
	}

	private List<InsuredPerson> syncContractorFromApplicant(List<InsuredPerson> insuredPeople, Applicant applicant) {
		if (applicant == null || insuredPeople == null) {
			return insuredPeople;
		}
		return insuredPeople.stream().map(person -> {
			if (Boolean.TRUE.equals(person.isContractor())) {
				return InsuredPerson.builder()
					.id(person.id())
					.planId(person.planId())
					.isContractor(true)
					.name(applicant.name())
					.residentNumber(applicant.residentNumber())
					.phone(applicant.phoneNumber())
					.email(applicant.email())
					.englishName(person.englishName())
					.passportNumber(person.passportNumber())
					.gender(person.gender())
					.individualPremium(person.individualPremium())
					.build();
			}
			return person;
		}).toList();
	}

	private ContractStatus resolveStatus(InsuranceContract existing, ModifyContract mc) {
		if (mc.status() != null) {
			return mc.status();
		}
		if (mc.statusName() != null) {
			return ContractStatus.COMPLETED;
		}
		return existing.status();
	}

	private PaymentInfo updatePayment(PaymentInfo existing, ContractPayment payment, String statusName) {
		if (payment == null && statusName == null) {
			return existing;
		}

		String currentStatus = (existing != null) ? existing.status() : null;
		String currentMethod = (existing != null) ? existing.method() : null;
		BigDecimal currentAmount = (existing != null) ? existing.totalAmount() : BigDecimal.ZERO;
		LocalDateTime currentPaidAt = (existing != null) ? existing.paidAt() : null;
		LocalDateTime currentCanceledAt = (existing != null) ? existing.canceledAt() : null;

		String resolvedStatus = currentStatus;
		if (payment != null && payment.status() != null) {
			resolvedStatus = payment.status();
		}
		else if (statusName != null) {
			resolvedStatus = resolvePaymentStatusByName(statusName, currentStatus);
		}

		return PaymentInfo.builder()
			.status(resolvedStatus)
			.method(payment != null && payment.method() != null ? payment.method() : currentMethod)
			.totalAmount(payment != null && payment.totalAmount() != null ? payment.totalAmount() : currentAmount)
			.paidAt(payment != null && payment.paidAt() != null ? payment.paidAt() : currentPaidAt)
			.canceledAt(payment != null ? payment.canceledAt() : currentCanceledAt)
			.build();
	}

	private String resolvePaymentStatusByName(String statusName, String currentStatus) {
		return switch (statusName) {
			case "임의해지" -> "CANCELED";
			case "가입완료" -> "COMPLETED";
			default -> currentStatus;
		};
	}

	private ContractMeta updateMeta(ContractMeta existing, ModifyContract mc) {
		InsurancePeriod updatedPeriod = existing.period();
		ContractPeriod period = mc.period();
		if (period != null) {
			updatedPeriod = InsurancePeriod.builder()
				.startDate(period.startDate() != null ? period.startDate() : existing.period().startDate())
				.endDate(period.endDate() != null ? period.endDate() : existing.period().endDate())
				.build();
		}

		SubscriptionOrigin updatedOrigin = updateSubscriptionOrigin(existing.origin(), mc.origin());

		return ContractMeta.builder()
			.policyNumber(mc.policyNumber() != null ? mc.policyNumber() : existing.policyNumber())
			.policyLink(mc.policyLink() != null ? mc.policyLink() : existing.policyLink())
			.origin(updatedOrigin)
			.applicationDate(mc.applicationDate() != null ? mc.applicationDate() : existing.applicationDate())
			.period(updatedPeriod)
			.build();
	}

	private SubscriptionOrigin updateSubscriptionOrigin(SubscriptionOrigin existing, ContractOrigin origin) {
		if (origin == null) {
			return existing;
		}

		return SubscriptionOrigin.builder()
			.insurerId(origin.insurerId() != null ? origin.insurerId() : existing.insurerId())
			.insurerName(origin.insurerName() != null ? origin.insurerName() : existing.insurerName())
			.insurerCode(existing.insurerCode())
			.channelId(origin.channelId() != null ? origin.channelId() : existing.channelId())
			.channelName(origin.channelName() != null ? origin.channelName() : existing.channelName())
			.channelCode(existing.channelCode())
			.partnerId(origin.partnerId() != null ? origin.partnerId() : existing.partnerId())
			.partnerName(origin.partnerName() != null ? origin.partnerName() : existing.partnerName())
			.partnerCode(existing.partnerCode())
			.build();
	}

	private ProductPlan updateProductPlan(InsuranceContract existing, ModifyContract mc) {
		ProductPlan existingPlan = existing.productPlan();
		PlanSelection ps = mc.plan();
		Long planId = (ps != null) ? ps.planId() : null;
		String travelCountry = (ps != null) ? ps.travelCountry() : null;
		String countryCode = (ps != null) ? ps.countryCode() : null;
		String planName = (ps != null) ? ps.planName() : null;
		Boolean silsonExclude = (ps != null) ? ps.silsonExclude() : null;

		if (planId == null && planName == null && travelCountry == null && countryCode == null) {
			return existingPlan;
		}

		String productNameResult = existingPlan.productName();
		String planNameResult = existingPlan.planName();
		Long resolvedPlanId = existingPlan.planId();

		if (planName != null && mc.applicant() != null && mc.applicant().residentNumber() != null) {
			Plan plan = planResolver.resolve(planName, mc.applicant().residentNumber(), silsonExclude);
			resolvedPlanId = plan.id();
			productNameResult = plan.fullName();
			planNameResult = plan.name();
		}
		else if (planName != null && existing.getContractor() != null
				&& existing.getContractor().residentNumber() != null) {
			Plan plan = planResolver.resolve(planName, existing.getContractor().residentNumber(), silsonExclude);
			resolvedPlanId = plan.id();
			productNameResult = plan.fullName();
			planNameResult = plan.name();
		}
		else if (planId != null) {
			Plan plan = planReader.read(planId).orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));
			resolvedPlanId = plan.id();
			productNameResult = plan.fullName();
			planNameResult = plan.name();
		}

		return ProductPlan.builder()
			.planId(resolvedPlanId)
			.productName(productNameResult)
			.planName(planNameResult)
			.travelCountry(travelCountry != null ? travelCountry : existingPlan.travelCountry())
			.countryCode(countryCode != null ? countryCode : existingPlan.countryCode())
			.coverageLink(existingPlan.coverageLink())
			.build();
	}

	private RefundInfo updateRefund(RefundInfo existing, ContractRefund refund) {
		if (refund == null) {
			return existing;
		}
		return RefundInfo.builder()
			.refundAmount(refund.refundAmount() != null ? refund.refundAmount()
					: (existing != null ? existing.refundAmount() : null))
			.refundMethod(refund.refundMethod() != null ? refund.refundMethod()
					: (existing != null ? existing.refundMethod() : null))
			.bankName(refund.bankName())
			.accountNumber(refund.accountNumber())
			.depositorName(refund.depositorName())
			.refundReason(refund.refundReason())
			.refundedAt(refund.refundedAt() != null ? refund.refundedAt()
					: (existing != null ? existing.refundedAt() : null))
			.build();
	}

	private Applicant updateApplicant(Applicant existing, ContractApplicant applicant) {
		if (applicant == null) {
			return existing;
		}

		return Applicant.builder()
			.name(applicant.name() != null ? applicant.name() : existing.name())
			.residentNumber(applicant.residentNumber() != null ? applicant.residentNumber() : existing.residentNumber())
			.phoneNumber(applicant.phoneNumber() != null ? applicant.phoneNumber() : existing.phoneNumber())
			.email(applicant.email() != null ? applicant.email() : existing.email())
			.build();
	}

	private List<InsuredPerson> updateInsuredPeople(List<InsuredPerson> existing,
			List<ModifyInsuredPerson> modifications) {
		if (modifications == null) {
			return existing;
		}
		if (modifications.isEmpty()) {
			return List.of();
		}

		Map<Long, InsuredPerson> existingMap = existing.stream()
			.filter(p -> p.id() != null)
			.collect(java.util.stream.Collectors.toMap(InsuredPerson::id, p -> p));

		List<InsuredPerson> result = modifications.stream()
			.map(m -> toInsuredPerson(m, existingMap.get(m.id())))
			.collect(java.util.ArrayList::new, java.util.ArrayList::add, java.util.ArrayList::addAll);

		// 기존 contractor가 수정 목록에 없으면 보존
		Set<Long> modifiedIds = modifications.stream()
			.map(ModifyInsuredPerson::id)
			.filter(java.util.Objects::nonNull)
			.collect(java.util.stream.Collectors.toSet());

		existing.stream()
			.filter(p -> Boolean.TRUE.equals(p.isContractor()))
			.filter(p -> !modifiedIds.contains(p.id()))
			.findFirst()
			.ifPresent(result::add);

		return result;
	}

	private InsuredPerson toInsuredPerson(ModifyInsuredPerson m, InsuredPerson existingPerson) {
		return InsuredPerson.builder()
			.id(m.id())
			.planId(existingPerson != null ? existingPerson.planId() : null)
			.isContractor(existingPerson != null ? existingPerson.isContractor() : null)
			.name(m.name())
			.englishName(m.englishName())
			.residentNumber(m.residentNumber())
			.passportNumber(m.passportNumber())
			.gender(m.gender())
			.individualPremium(m.premium())
			.build();
	}

}