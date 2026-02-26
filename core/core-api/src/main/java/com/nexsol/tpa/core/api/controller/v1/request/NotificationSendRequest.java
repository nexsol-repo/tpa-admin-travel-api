package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.notification.NotificationSendCommand;
import com.nexsol.tpa.core.enums.NotificationType;

/**
 * 알림 발송 요청 DTO
 */
public record NotificationSendRequest(NotificationType type, String link) {

	public NotificationSendCommand toCommand(Long contractId) {
		return NotificationSendCommand.builder().contractId(contractId).type(type).link(link).build();
	}

}
