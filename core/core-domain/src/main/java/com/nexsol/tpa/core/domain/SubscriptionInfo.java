package com.nexsol.tpa.core.domain;

public record SubscriptionInfo(String partner, String channel, String insurer) {
    public static SubscriptionInfo toSubscriptionInfo(ContractMeta meta) {
        return new SubscriptionInfo(meta.origin().partnerName(), meta.origin().channelName(),
                meta.origin().insurerName());
    }
}