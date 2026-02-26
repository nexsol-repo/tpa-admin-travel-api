package com.nexsol.tpa.core.domain.contract;

import java.math.BigDecimal;

public record ContractStatusInfo(String statusName, // 한글 상태명 (가입완료)
		String statusCode, // 코드 (COMPLETED)
		int insuredCount, // 총 인원
		BigDecimal totalPremium // 총 보험료
) {
	public static ContractStatusInfo toContractStatusInfo(InsuranceContract domain) {
		BigDecimal total = (domain.paymentInfo() != null) ? domain.paymentInfo().totalAmount() : BigDecimal.ZERO;
		return new ContractStatusInfo(domain.status().name(), // 실제로는 Enum의 description 사용
																// 권장
				domain.status().name(), domain.insuredPeople().size(), total);
	}
}