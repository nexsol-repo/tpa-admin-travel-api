package com.nexsol.tpa.core.domain.notification;

import com.nexsol.tpa.core.enums.NotificationType;
import lombok.Builder;

/**
 * 알림 발송 명령 객체
 */
@Builder
public record NotificationSendCommand(Long contractId, NotificationType type, String link) {

}
