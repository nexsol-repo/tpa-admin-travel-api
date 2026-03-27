package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

@Builder
public record ContractOrigin(Long partnerId, String partnerName, Long channelId, String channelName, Long insurerId,
		String insurerName) {
}