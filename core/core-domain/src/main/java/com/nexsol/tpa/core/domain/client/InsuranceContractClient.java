package com.nexsol.tpa.core.domain.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InsuranceContractClient {

	SubscriptionResult estimateSave(EstimateSaveCommand cmd);

	void cancelContract(String company, String polNo, String quotGrpNo, String quotReqNo);

	CertificateLinkResult issueCertificate(String company, String polNo, String pdCd, String quotGrpNo,
			String quotReqNo, String otptDiv, String otptTpCd);

	BridgeApiResult issueCertificateRaw(String company, String polNo, String pdCd, String quotGrpNo, String quotReqNo,
			String otptDiv, String otptTpCd);

	BridgeApiResult contractList(String company, Map<String, Object> bodyFields);

	BridgeApiResult contractDetail(String company, Map<String, Object> bodyFields);

	record BridgeApiResult(boolean success, String errCd, String errMsg, Object data) {
	}

	record SubscriptionResult(boolean success, BigDecimal ttPrem, String polNo, String quotGrpNo, String quotReqNo,
			String errCd, String errMsg, Object rawData) {

		public static SubscriptionResult success(BigDecimal ttPrem, String polNo, String quotGrpNo, String quotReqNo,
				Object rawData) {
			return new SubscriptionResult(true, ttPrem, polNo, quotGrpNo, quotReqNo, null, null, rawData);
		}

		public static SubscriptionResult fail(String errCd, String errMsg, Object rawData) {
			return new SubscriptionResult(false, null, null, null, null, errCd, errMsg, rawData);
		}
	}

	record CertificateLinkResult(String rltLinkUrl) {
	}

	record EstimateSaveCommand(String company, String polNo, String pdCd, String untPdCd, String sbcpDt,
			String insBgnDt, String insEdDt, String trvArCd, List<InsuredPersonCommand> insuredPeople,
			String grupSalChnDivCd, String cardNo, String efctPrd, String dporNm, String dporCd) {

		public record InsuredPersonCommand(String inspeBdt, String gndrCd, String inspeNm, String engInspeNm,
				String planGrpCd, String planCd) {
		}
	}

}