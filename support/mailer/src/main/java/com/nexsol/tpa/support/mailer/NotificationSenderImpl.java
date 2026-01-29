package com.nexsol.tpa.support.mailer;

import com.nexsol.tpa.client.aligo.SmsSender;
import com.nexsol.tpa.core.domain.notification.NotificationSender;
import com.nexsol.tpa.core.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 알림 발송 도구 구현체 (Implement Layer) SmsSender와 EmailSender를 사용하여 SMS/Email 발송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSenderImpl implements NotificationSender {

    private final SmsSender smsSender;

    private final TravelEmailSender travelEmailSender;

    @Override
    public void sendEmail(String toEmail, NotificationType type, String link, String name) {
        travelEmailSender.send(toEmail, type, link, name);
        log.info("이메일 발송 완료: {} -> {}", type, toEmail);
    }

    @Override
    public void sendSms(String phoneNumber, NotificationType type, String link, String name) {
        String message = type.formatSmsMessage(name, link);
        smsSender.sendSms(phoneNumber, message);
        log.info("SMS 발송 완료: {} -> {}", type, phoneNumber);
    }

}
