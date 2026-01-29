package com.nexsol.tpa.core.domain.notification;

import com.nexsol.tpa.core.enums.ServiceType;

/**
 * 알림 이력 등록 도구 인터페이스 (Implement Layer) SMS/Mail 발송 이력 등록을 담당
 */
public interface NotificationHistoryRegistrar {

    /**
     * SMS 발송 이력 등록
     */
    void registerSms(Long contractId, String message, ServiceType serviceType);

    /**
     * 이메일 발송 이력 등록
     */
    void registerEmail(Long contractId, String message, ServiceType serviceType);

}
