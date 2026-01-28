package com.nexsol.tpa.core.domain.channel;

/**
 * 채널 도메인 모델
 */
public record Channel(
        Long id,
        String code,
        String name
) {

}
