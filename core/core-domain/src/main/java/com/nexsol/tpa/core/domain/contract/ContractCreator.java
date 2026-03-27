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
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContractCreator {

	private final PlanReader planReader;

	private final PlanResolver planResolver;

	public InsuranceContract create(NewContract newContract) {
		return buildContract(newContract);
	}

	private InsuranceContract buildContract(NewContract nc) {
		return InsuranceContract.builder()
			.contractId(null)
			.status(nc.status() != null ? nc.status() : ContractStatus.PENDING)
			.metaInfo(buildContractMeta(nc))
			.productPlan(buildProductPlan(nc))
			.applicant(buildApplicant(nc.applicant()))
			.paymentInfo(buildPaymentInfo(nc.payment()))
			.refundInfo(buildRefundInfo(nc.refund()))
			.insuredPeople(buildInsuredPeople(nc))
			.employeeId(nc.employeeId())
			.build();
	}

	private ContractMeta buildContractMeta(NewContract nc) {
		ContractOrigin origin = nc.origin();
		ContractPeriod period = nc.period();
		return ContractMeta.builder()
			.policyNumber(nc.policyNumber())
			.origin(origin != null ? SubscriptionOrigin.builder()
				.partnerId(origin.partnerId())
				.partnerName(origin.partnerName())
				.channelId(origin.channelId())
				.channelName(origin.channelName())
				.insurerId(origin.insurerId())
				.insurerName(origin.insurerName())
				.build() : null)
			.applicationDate(nc.applicationDate())
			.period(period != null
					? InsurancePeriod.builder().startDate(period.startDate()).endDate(period.endDate()).build() : null)
			.build();
	}

	private ProductPlan buildProductPlan(NewContract nc) {
		Plan plan = resolvePlan(nc);
		PlanSelection ps = nc.plan();
		return ProductPlan.builder()
			.planId(plan.id())
			.familyId(plan.familyId())
			.productName(plan.fullName())
			.planName(plan.name())
			.travelCountry(ps != null ? ps.travelCountry() : null)
			.countryCode(ps != null ? ps.countryCode() : null)
			.build();
	}

	private Plan resolvePlan(NewContract nc) {
		PlanSelection ps = nc.plan();
		if (ps != null && ps.planName() != null && nc.applicant() != null && nc.applicant().residentNumber() != null) {
			return planResolver.resolve(ps.planName(), nc.applicant().residentNumber(), ps.silsonExclude());
		}
		Long planId = (ps != null) ? ps.planId() : null;
		return planReader.read(planId).orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));
	}

	private Applicant buildApplicant(ContractApplicant applicant) {
		if (applicant == null) {
			return null;
		}
		return Applicant.builder()
			.name(applicant.name())
			.residentNumber(applicant.residentNumber())
			.phoneNumber(applicant.phoneNumber())
			.email(applicant.email())
			.build();
	}

	private PaymentInfo buildPaymentInfo(ContractPayment payment) {
		if (payment == null) {
			return null;
		}
		return PaymentInfo.builder()
			.method(payment.method())
			.totalAmount(payment.totalAmount() != null ? payment.totalAmount() : BigDecimal.ZERO)
			.paidAt(payment.paidAt())
			.canceledAt(payment.canceledAt())
			.build();
	}

	private RefundInfo buildRefundInfo(ContractRefund refund) {
		if (refund == null) {
			return null;
		}
		return RefundInfo.builder()
			.refundAmount(refund.refundAmount())
			.refundMethod(refund.refundMethod())
			.bankName(refund.bankName())
			.accountNumber(refund.accountNumber())
			.depositorName(refund.depositorName())
			.refundReason(refund.refundReason())
			.refundedAt(refund.refundedAt())
			.build();
	}

	private List<InsuredPerson> buildInsuredPeople(NewContract nc) {
		List<InsuredPerson> insuredPeople = new java.util.ArrayList<>();

		if (nc.applicant() != null) {
			BigDecimal premium = (nc.payment() != null && nc.payment().totalAmount() != null)
					? nc.payment().totalAmount() : BigDecimal.ZERO;
			insuredPeople.add(InsuredPerson.builder()
				.name(nc.applicant().name())
				.residentNumber(nc.applicant().residentNumber())
				.phone(nc.applicant().phoneNumber())
				.email(nc.applicant().email())
				.isContractor(true)
				.individualPremium(premium)
				.build());
		}

		if (nc.companions() != null && !nc.companions().isEmpty()) {
			nc.companions().stream().map(this::buildInsuredPerson).forEach(insuredPeople::add);
		}

		return insuredPeople;
	}

	private InsuredPerson buildInsuredPerson(ContractCompanion companion) {
		String fullEnglishName = buildFullEnglishName(companion.englishLastName(), companion.englishName());
		return InsuredPerson.builder()
			.name(companion.name())
			.englishName(fullEnglishName)
			.residentNumber(companion.residentNumber())
			.passportNumber(companion.passportNumber())
			.gender(companion.gender())
			.isContractor(false)
			.individualPremium(companion.premium())
			.build();
	}

	private String buildFullEnglishName(String lastName, String firstName) {
		if (lastName == null && firstName == null) {
			return null;
		}
		if (lastName == null) {
			return firstName;
		}
		if (firstName == null) {
			return lastName;
		}
		return lastName + " " + firstName;
	}

}