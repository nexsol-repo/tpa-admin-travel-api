package com.nexsol.tpa.core.domain.subscription;

import lombok.Builder;

@Builder
public record SubscriptionOrigin(Long partnerId, String partnerName, String partnerCode, Long channelId,
        String channelName, String channelCode, Long insurerId, String insurerName, String insurerCode

) {
}
