package com.nexsol.tpa.core.domain.channel;

import com.nexsol.tpa.core.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 채널 서비스 (Business Layer) - 비즈니스 흐름을 중계하는 Coordinator 역할
 */
@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelReader channelReader;

    public List<Channel> getActiveChannels() {
        return channelReader.readAllActive();
    }

    public List<Channel> getChannels(Long partnerId, ServiceType serviceType) {
        return channelReader.readAll(partnerId, serviceType);
    }

}