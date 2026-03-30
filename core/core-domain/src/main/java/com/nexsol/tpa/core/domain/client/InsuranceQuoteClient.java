package com.nexsol.tpa.core.domain.client;

import java.util.List;

public interface InsuranceQuoteClient {

	String calculatePremium(PremiumCommand command);

	record PremiumCommand(String company, String productCode, String unitProductCode, String sbcpDt, String insBgnDt,
			String insEdDt, String trvArCd, List<InsuredInfo> insuredList) {

		public record InsuredInfo(String planGroupCode, String planCode, String birth, String gender, String name,
				String nameEng) {
		}
	}

}