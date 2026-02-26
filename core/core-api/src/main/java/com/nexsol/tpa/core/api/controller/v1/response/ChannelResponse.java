package com.nexsol.tpa.core.api.controller.v1.response;

import com.nexsol.tpa.core.domain.channel.Channel;

public record ChannelResponse(Long id, String value, String label) {

	public static ChannelResponse of(Channel channel) {
		return new ChannelResponse(channel.id(), channel.code(), channel.name());
	}

}
