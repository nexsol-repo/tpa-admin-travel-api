package com.nexsol.tpa.core.domain.client;

import com.nexsol.tpa.core.domain.client.InsuranceContractClient.BridgeApiResult;

public interface InsuranceReferenceClient {

	BridgeApiResult getPlans(String stdDt);

	BridgeApiResult getCityNationCodes(String keyword, String type);

}