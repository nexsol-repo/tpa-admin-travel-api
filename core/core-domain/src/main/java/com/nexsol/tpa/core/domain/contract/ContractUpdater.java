package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 계약 수정 도구 클래스 (Implement Layer) 각 도메인별 수정은 전문 도구 클래스에 위임
 * <ul>
 * <li>플랜: ContractPlanUpdater</li>
 * <li>결제/환불/상태: ContractPaymentUpdater</li>
 * <li>피보험자 병합: ContractInsuredPeopleUpdater</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class ContractUpdater {

	private final ContractPlanUpdater contractPlanUpdater;

	private final ContractPaymentUpdater contractPaymentUpdater;

	private final ContractInsuredPeopleUpdater insuredPeopleMerger;

	public InsuranceContract update(InsuranceContract existing, ModifyContract mc) {
		Applicant updatedApplicant = updateApplicant(existing.applicant(), mc.applicant());
		List<InsuredPerson> updatedInsuredPeople = insuredPeopleMerger.merge(existing.insuredPeople(),
				mc.insuredPeople());

		if (mc.applicant() != null) {
			updatedInsuredPeople = insuredPeopleMerger.syncContractorFromApplicant(updatedInsuredPeople,
					updatedApplicant);
		}

		boolean planChanged = mc.plan() != null && mc.plan().planName() != null;
		if (planChanged) {
			LocalDateTime applicationDate = resolveApplicationDate(existing, mc);
			updatedInsuredPeople = contractPlanUpdater.resolveInsuredPeoplePlan(updatedInsuredPeople, mc.plan(),
					applicationDate);
		}

		return buildUpdatedContract(existing, mc, updatedApplicant, updatedInsuredPeople);
	}

	private InsuranceContract buildUpdatedContract(InsuranceContract existing, ModifyContract mc,
			Applicant updatedApplicant, List<InsuredPerson> updatedInsuredPeople) {
		return InsuranceContract.builder()
			.contractId(existing.contractId())
			.status(contractPaymentUpdater.resolveStatus(existing, mc))
			.metaInfo(updateMeta(existing.metaInfo(), mc))
			.productPlan(contractPlanUpdater.updateProductPlan(existing, mc))
			.applicant(updatedApplicant)
			.paymentInfo(contractPaymentUpdater.updatePayment(existing.paymentInfo(), mc.payment(), mc.statusName()))
			.refundInfo(contractPaymentUpdater.updateRefund(existing.refundInfo(), mc.refund()))
			.insuredPeople(updatedInsuredPeople)
			.employeeId(nvl(mc.employeeId(), existing.employeeId()))
			.totalPremium(nvl(mc.totalPremium(), existing.totalPremium()))
			.build();
	}

	private LocalDateTime resolveApplicationDate(InsuranceContract existing, ModifyContract mc) {
		if (mc.applicationDate() != null) {
			return mc.applicationDate();
		}
		if (existing.metaInfo() != null) {
			return existing.metaInfo().applicationDate();
		}
		return null;
	}

	private ContractMeta updateMeta(ContractMeta existing, ModifyContract mc) {
		return ContractMeta.builder()
			.policyNumber(nvl(mc.policyNumber(), existing.policyNumber()))
			.policyLink(nvl(mc.policyLink(), existing.policyLink()))
			.origin(updateSubscriptionOrigin(existing.origin(), mc.origin()))
			.applicationDate(nvl(mc.applicationDate(), existing.applicationDate()))
			.period(updatePeriod(existing.period(), mc.period()))
			.build();
	}

	private InsurancePeriod updatePeriod(InsurancePeriod existing, ContractPeriod period) {
		if (period == null) {
			return existing;
		}
		return InsurancePeriod.builder()
			.startDate(nvl(period.startDate(), existing.startDate()))
			.endDate(nvl(period.endDate(), existing.endDate()))
			.build();
	}

	private SubscriptionOrigin updateSubscriptionOrigin(SubscriptionOrigin existing, ContractOrigin origin) {
		if (origin == null) {
			return existing;
		}
		return SubscriptionOrigin.builder()
			.insurerId(nvl(origin.insurerId(), existing.insurerId()))
			.insurerName(nvl(origin.insurerName(), existing.insurerName()))
			.insurerCode(existing.insurerCode())
			.channelId(nvl(origin.channelId(), existing.channelId()))
			.channelName(nvl(origin.channelName(), existing.channelName()))
			.channelCode(existing.channelCode())
			.partnerId(nvl(origin.partnerId(), existing.partnerId()))
			.partnerName(nvl(origin.partnerName(), existing.partnerName()))
			.partnerCode(existing.partnerCode())
			.build();
	}

	private Applicant updateApplicant(Applicant existing, ContractApplicant applicant) {
		if (applicant == null) {
			return existing;
		}
		return Applicant.builder()
			.name(nvl(applicant.name(), existing.name()))
			.residentNumber(nvl(applicant.residentNumber(), existing.residentNumber()))
			.phoneNumber(nvl(applicant.phoneNumber(), existing.phoneNumber()))
			.email(nvl(applicant.email(), existing.email()))
			.premium(nvl(applicant.premium(), existing.premium()))
			.build();
	}

	/**
	 * 새 값이 null이 아니면 새 값, null이면 기존값 반환 (partial update 패턴)
	 */
	private <T> T nvl(T newValue, T existingValue) {
		if (newValue != null) {
			return newValue;
		}
		return existingValue;
	}

}