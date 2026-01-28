package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.channel.Channel;
import com.nexsol.tpa.core.domain.channel.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChannelRepositoryImpl implements ChannelRepository {

    private final ChannelJpaRepository channelJpaRepository;

    @Override
    public List<Channel> findAllActive() {
        return channelJpaRepository.findByIsActiveTrue()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Channel toDomain(TravelChannelEntity entity) {
        return new Channel(
                entity.getId(),
                entity.getChannelCode(),
                entity.getChannelName()
        );
    }

}
