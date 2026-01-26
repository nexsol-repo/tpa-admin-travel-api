package com.nexsol.tpa.core.domain;

import lombok.Builder;

@Builder
public record SubscriptionOrigin(String partnerName, String channelName, String insurerName

) {
}
