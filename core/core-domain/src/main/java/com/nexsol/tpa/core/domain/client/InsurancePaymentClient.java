package com.nexsol.tpa.core.domain.client;

import com.nexsol.tpa.core.domain.client.InsuranceContractClient.BridgeApiResult;

public interface InsurancePaymentClient {

	BridgeApiResult approveCard(String company, String polNo, String quotGrpNo, String quotReqNo, String crdNo,
			String efctPrd, String dporNm, String dporCd, String rcptPrem);

	BridgeApiResult cancelCard(String company, String polNo, String estNo, String orgApvNo, String cncAmt);

}