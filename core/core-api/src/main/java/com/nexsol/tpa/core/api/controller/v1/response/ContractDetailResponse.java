package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.applicant.ApplicantInfo;
import com.nexsol.tpa.core.domain.applicant.CompanionInfo;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.contract.InsuranceSection;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.payment.RefundInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record ContractDetailResponse(Long contractId, InsuranceSection insuranceSection, ApplicantInfo applicantSection,
		PaymentInfo payment, RefundInfo refund, List<CompanionInfo> companions) {

	public static ContractDetailResponse of(InsuranceContract domain) {
		List<CompanionInfo> companions = (domain.insuredPeople() != null && domain.insuredPeople().size() > 1)
				? domain.insuredPeople().stream().map(CompanionInfo::of).toList() : List.of();

		return ContractDetailResponse.builder()
			.contractId(domain.contractId())
			.insuranceSection(InsuranceSection.toInsuranceSection(domain))
			.applicantSection(ApplicantInfo.toApplicantInfo(domain.applicant()))
			.payment(PaymentInfo.toPaymentInfo(domain.paymentInfo()))
			.refund(domain.refundInfo())
			.companions(companions)
			.build();
	}
}
