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
		return ContractDetailResponse.builder()
			.contractId(domain.contractId())
			.insuranceSection(InsuranceSection.toInsuranceSection(domain))
			.applicantSection(ApplicantInfo.fromInsuredPerson(domain.getContractor()))
			.payment(PaymentInfo.toPaymentInfo(domain.paymentInfo()))
			.refund(domain.refundInfo())
			.companions(domain.getCompanions().stream().map(CompanionInfo::of).toList())
			.build();
	}
}
