package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ContractResponse(Long contractId, String contractStatus, String contractStatusCode, String policyNumber,
		String partnerName, String channelName, String insurerName, String applicantName, String applicantPhone,
		int insuredCount, BigDecimal totalPremium, LocalDateTime applicationDate, LocalDate insuranceStartDate,
		LocalDate insuranceEndDate) {
	public static ContractResponse of(InsuranceContract domain) {
		String displayStatus = resolveDisplayStatus(domain);
		String displayStatusCode = resolveDisplayStatusCode(domain);
		InsuredPerson contractor = domain.getContractor();
		return ContractResponse.builder()
			.contractId(domain.contractId())
			.contractStatus(displayStatus)
			.contractStatusCode(displayStatusCode)
			.policyNumber(domain.metaInfo().policyNumber())
			.partnerName(domain.metaInfo().origin().partnerName())
			.channelName(domain.metaInfo().origin().channelName())
			.insurerName(domain.metaInfo().origin().insurerName())
			.applicantName(contractor != null ? contractor.name() : null)
			.applicantPhone(contractor != null ? contractor.phone() : null)
			.insuredCount(domain.getTotalInsuredCount())
			.totalPremium(domain.totalPremium() != null ? domain.totalPremium() : BigDecimal.ZERO)
			.applicationDate(domain.metaInfo().applicationDate())
			.insuranceStartDate(domain.metaInfo().period().startDate())
			.insuranceEndDate(domain.metaInfo().period().endDate())
			.build();
	}

	private static String resolveDisplayStatus(InsuranceContract domain) {
		if (domain.paymentInfo() != null && "CANCELED".equals(domain.paymentInfo().status())) {
			return "임의해지";
		}
		return domain.status().getDescription();
	}

	private static String resolveDisplayStatusCode(InsuranceContract domain) {
		if (domain.paymentInfo() != null && "CANCELED".equals(domain.paymentInfo().status())) {
			return "CANCELED";
		}
		return domain.status().name();
	}
}