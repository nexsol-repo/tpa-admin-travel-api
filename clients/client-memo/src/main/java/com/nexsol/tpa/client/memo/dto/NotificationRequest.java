package com.nexsol.tpa.client.memo.dto;

import com.nexsol.tpa.core.enums.ServiceType;
import lombok.Builder;
import lombok.Data;

/**
 * 알림 이력 등록 요청 DTO
 */
@Data
@Builder
public class NotificationRequest {

    /**
     * 이력 유형 (MEMO, SYSTEM, SMS, MAIL, ALARM)
     */
    private String type;

    /**
     * 알림 내용
     */
    private String content;

    /**
     * 서비스 유형
     */
    private ServiceType serviceType;

}
