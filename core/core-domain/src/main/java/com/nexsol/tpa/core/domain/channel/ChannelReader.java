package com.nexsol.tpa.core.domain.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 채널 조회 도구 클래스 (Implement Layer)
 */
@Component
@RequiredArgsConstructor
public class ChannelReader {

    private final ChannelRepository channelRepository;

    public List<Channel> readAllActive() {
        return channelRepository.findAllActive();
    }

}
