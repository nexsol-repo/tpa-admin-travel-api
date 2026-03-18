package com.nexsol.tpa.core.domain.contract;

import java.math.BigDecimal;

public record ContractStatusInfo(String statusName, // 한글 상태명 (가입완료, 임의해지)
		String statusCode, // 코드 (COMPLETED, CANCELED)
		int insuredCount, // 총 인원
		BigDecimal totalPremium // 총 보험료
) {
	public static ContractStatusInfo toContractStatusInfo(InsuranceContract domain) {
		BigDecimal total = (domain.paymentInfo() != null) ? domain.paymentInfo().totalAmount() : BigDecimal.ZERO;
		String displayName = resolveDisplayName(domain);
		String displayCode = resolveDisplayCode(domain);
		return new ContractStatusInfo(displayName, displayCode, domain.insuredPeople().size(), total);
	}

	private static String resolveDisplayName(InsuranceContract domain) {
		if (domain.paymentInfo() != null && "CANCELED".equals(domain.paymentInfo().status())) {
			return "임의해지";
		}
		return domain.status().getDescription();
	}

	private static String resolveDisplayCode(InsuranceContract domain) {
		if (domain.paymentInfo() != null && "CANCELED".equals(domain.paymentInfo().status())) {
			return "CANCELED";
		}
		return domain.status().name();
	}
}