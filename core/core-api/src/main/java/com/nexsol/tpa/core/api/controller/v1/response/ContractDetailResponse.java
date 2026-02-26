package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.applicant.ApplicantInfo;
import com.nexsol.tpa.core.domain.applicant.CompanionInfo;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.contract.InsuranceSection;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import lombok.Builder;

import java.util.List;

@Builder
public record ContractDetailResponse(Long contractId, InsuranceSection insuranceSection, ApplicantInfo applicantSection,
		PaymentInfo paymentSection, List<CompanionInfo> companions) {

	public static ContractDetailResponse of(InsuranceContract domain) {
		return ContractDetailResponse.builder()
			.contractId(domain.contractId())
			.insuranceSection(InsuranceSection.toInsuranceSection(domain))
			.applicantSection(ApplicantInfo.toApplicantInfo(domain.applicant()))
			.paymentSection(PaymentInfo.toPaymentInfo(domain.paymentInfo()))
			.companions(domain.insuredPeople().stream().map(CompanionInfo::of).toList())
			.build();
	}
}
