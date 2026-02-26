package com.nexsol.tpa.core.domain.notification;

import com.nexsol.tpa.core.enums.NotificationType;

/**
 * 알림 발송 도구 인터페이스 (Implement Layer) SMS/Email 발송의 상세 구현을 담당
 */
public interface NotificationSender {

	/**
	 * 이메일 발송
	 * @param toEmail 수신자 이메일
	 * @param type 알림 유형
	 * @param link 링크 URL
	 * @param name 수신자 이름
	 */
	void sendEmail(String toEmail, NotificationType type, String link, String name);

	/**
	 * SMS 발송
	 * @param phoneNumber 수신자 전화번호
	 * @param type 알림 유형
	 * @param link 링크 URL
	 * @param name 수신자 이름
	 */
	void sendSms(String phoneNumber, NotificationType type, String link, String name);

}
