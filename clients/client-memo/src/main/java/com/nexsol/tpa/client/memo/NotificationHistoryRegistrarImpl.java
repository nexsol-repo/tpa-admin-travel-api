package com.nexsol.tpa.client.memo;

import com.nexsol.tpa.client.memo.dto.NotificationRequest;
import com.nexsol.tpa.core.domain.notification.NotificationHistoryRegistrar;
import com.nexsol.tpa.core.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 알림 이력 등록 도구 구현체 (client 모듈) MemoClient를 사용하여 알림 이력 등록
 */
@Component
@RequiredArgsConstructor
public class NotificationHistoryRegistrarImpl implements NotificationHistoryRegistrar {

    private final MemoClient memoClient;

    @Override
    public void registerSms(Long contractId, String message, ServiceType serviceType) {
        NotificationRequest request = NotificationRequest.builder()
            .type("SMS")
            .message(message)
            .serviceType(serviceType)
            .build();
        memoClient.createNotification(contractId, request);
    }

    @Override
    public void registerEmail(Long contractId, String message, ServiceType serviceType) {
        NotificationRequest request = NotificationRequest.builder()
            .type("MAIL")
            .message(message)
            .serviceType(serviceType)
            .build();
        memoClient.createNotification(contractId, request);
    }

}
