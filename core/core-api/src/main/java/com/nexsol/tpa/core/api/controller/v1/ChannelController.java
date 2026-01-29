package com.nexsol.tpa.core.api.controller.v1;

import com.nexsol.tpa.core.api.controller.v1.response.ChannelResponse;
import com.nexsol.tpa.core.domain.channel.ChannelService;
import com.nexsol.tpa.core.enums.ServiceType;
import com.nexsol.tpa.core.support.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/travel")
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping("/partner/{partnerId}/channel")
    public ApiResponse<List<ChannelResponse>> getChannels(@PathVariable Long partnerId) {
        List<ChannelResponse> channels = channelService.getChannels(partnerId, ServiceType.TRAVEL)
            .stream()
            .map(ChannelResponse::of)
            .toList();

        return ApiResponse.success(channels);
    }

}