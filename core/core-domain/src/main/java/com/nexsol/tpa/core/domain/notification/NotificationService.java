package com.nexsol.tpa.core.domain.notification;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.contract.ContractReader;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.enums.NotificationType;
import com.nexsol.tpa.core.enums.ServiceType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 알림 서비스 (Business Layer - Coordinator) SMS/Email 발송과 알림 이력 등록을 하나의 트랜잭션으로 관리
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

	private static final ServiceType DEFAULT_SERVICE_TYPE = ServiceType.TRAVEL;

	private final ContractReader contractReader;

	private final NotificationSender notificationSender;

	private final NotificationHistoryRegistrar notificationHistoryRegistrar;

	/**
	 * 이메일 발송 + 알림 이력 등록
	 * @param command 알림 발송 명령
	 */
	@Transactional
	public void sendEmail(NotificationSendCommand command) {
		// 1. 계약 정보 조회 (수신자 정보 확인용)
		InsuranceContract contract = contractReader.read(command.contractId());
		InsuredPerson contractor = contract.getContractor();

		// 2. 이메일 발송
		notificationSender.sendEmail(contractor.email(), command.type(), command.link(), contractor.name());

		// 3. 알림 이력 등록
		String message = generateMessage(command.type());
		notificationHistoryRegistrar.registerEmail(command.contractId(), message, DEFAULT_SERVICE_TYPE);
	}

	/**
	 * SMS 발송 + 알림 이력 등록
	 * @param command 알림 발송 명령
	 */
	@Transactional
	public void sendSms(NotificationSendCommand command) {
		// 1. 계약 정보 조회 (수신자 정보 확인용)
		InsuranceContract contract = contractReader.read(command.contractId());
		InsuredPerson contractor = contract.getContractor();

		// 2. SMS 발송
		notificationSender.sendSms(contractor.phone(), command.type(), command.link(), contractor.name());

		// 3. 알림 이력 등록
		String message = generateMessage(command.type());
		notificationHistoryRegistrar.registerSms(command.contractId(), message, DEFAULT_SERVICE_TYPE);
	}

	/**
	 * 이메일 + SMS 동시 발송 + 알림 이력 등록
	 * @param command 알림 발송 명령
	 */
	@Transactional
	public void sendAll(NotificationSendCommand command) {
		// 1. 계약 정보 조회
		InsuranceContract contract = contractReader.read(command.contractId());
		InsuredPerson contractor = contract.getContractor();
		String name = contractor.name();

		// 2. 이메일 발송
		notificationSender.sendEmail(contractor.email(), command.type(), command.link(), name);

		// 3. SMS 발송
		notificationSender.sendSms(contractor.phone(), command.type(), command.link(), name);

		// 4. 알림 이력 등록 (각각)
		String message = generateMessage(command.type());
		notificationHistoryRegistrar.registerEmail(command.contractId(), message, DEFAULT_SERVICE_TYPE);
		notificationHistoryRegistrar.registerSms(command.contractId(), message, DEFAULT_SERVICE_TYPE);
	}

	private String generateMessage(NotificationType type) {
		return switch (type) {
			case REJOIN -> "재가입 안내 발송";
			case CERTIFICATE -> "가입확인서 안내 발송";
		};
	}

}
