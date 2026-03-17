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

	private final ContractRepository contractRepository;

	private final PlanReader planReader;

	private final PlanResolver planResolver;

	public Long create(ContractCreateCommand command) {
		InsuranceContract newContract = buildContract(command);
		return contractRepository.create(newContract);
	}

	private InsuranceContract buildContract(ContractCreateCommand command) {
		return InsuranceContract.builder()
			.contractId(null) // 신규 생성이므로 null
			.status(command.status() != null ? command.status() : ContractStatus.PENDING)
			.metaInfo(buildContractMeta(command))
			.productPlan(buildProductPlan(command))
			.applicant(buildApplicant(command.applicant()))
			.paymentInfo(buildPaymentInfo(command.payment()))
			.refundInfo(buildRefundInfo(command.refund()))
			.insuredPeople(buildInsuredPeople(command))
			.employeeId(command.employeeId())
			.build();
	}

	private ContractMeta buildContractMeta(ContractCreateCommand command) {
		return ContractMeta.builder()
			.policyNumber(command.policyNumber())
			.origin(buildSubscriptionOrigin(command.subscriptionOrigin()))
			.applicationDate(command.applicationDate())
			.period(buildInsurancePeriod(command.period()))
			.build();
	}

	private SubscriptionOrigin buildSubscriptionOrigin(ContractCreateCommand.SubscriptionOriginCommand origin) {
		if (origin == null) {
			return null;
		}
		return SubscriptionOrigin.builder()
			.partnerId(origin.partnerId())
			.partnerName(origin.partnerName())
			.channelId(origin.channelId())
			.channelName(origin.channelName())
			.insurerId(origin.insurerId())
			.insurerName(origin.insurerName())
			.build();
	}

	private InsurancePeriod buildInsurancePeriod(ContractCreateCommand.PeriodCommand period) {
		if (period == null) {
			return null;
		}
		return InsurancePeriod.builder().startDate(period.startDate()).endDate(period.endDate()).build();
	}

	private ProductPlan buildProductPlan(ContractCreateCommand command) {
		Plan plan = resolvePlan(command);

		return ProductPlan.builder()
			.planId(plan.id())
			.productName(plan.fullName())
			.planName(plan.name())
			.travelCountry(command.travelCountry())
			.countryCode(command.countryCode())
			.build();
	}

	/**
	 * planName이 있으면 가입자 주민번호로 만나이를 계산하여 플랜을 찾고, 없으면 기존처럼 planId로 직접 조회한다.
	 */
	private Plan resolvePlan(ContractCreateCommand command) {
		if (command.planName() != null && command.applicant() != null && command.applicant().residentNumber() != null) {
			return planResolver.resolve(command.planName(), command.applicant().residentNumber(),
					command.silsonExclude());
		}
		return planReader.read(command.planId()).orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));
	}

	private Applicant buildApplicant(ContractCreateCommand.ApplicantCommand applicant) {
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

	private PaymentInfo buildPaymentInfo(ContractCreateCommand.PaymentCommand payment) {
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

	private RefundInfo buildRefundInfo(ContractCreateCommand.RefundCommand refund) {
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

	private List<InsuredPerson> buildInsuredPeople(ContractCreateCommand command) {
		if (command.companions() == null || command.companions().isEmpty()) {
			// 동반자 정보가 없으면 가입자 본인을 피보험자로 등록
			if (command.applicant() != null) {
				BigDecimal premium = (command.payment() != null && command.payment().totalAmount() != null)
						? command.payment().totalAmount() : BigDecimal.ZERO;
				return List.of(InsuredPerson.builder()
					.name(command.applicant().name())
					.residentNumber(command.applicant().residentNumber())
					.individualPremium(premium)
					.build());
			}
			return List.of();
		}

		return command.companions().stream().map(this::buildInsuredPerson).toList();
	}

	private InsuredPerson buildInsuredPerson(ContractCreateCommand.CompanionCommand companion) {
		String fullEnglishName = buildFullEnglishName(companion.englishLastName(), companion.englishName());

		return InsuredPerson.builder()
			.name(companion.name())
			.englishName(fullEnglishName)
			.residentNumber(companion.residentNumber())
			.passportNumber(companion.passportNumber())
			.gender(companion.gender())
			.individualPremium(companion.premium())
			.individualPolicyNumber(companion.policyNumber())
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
