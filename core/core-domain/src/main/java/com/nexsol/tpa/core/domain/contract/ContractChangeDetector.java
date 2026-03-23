package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 계약 변경 감지 도구 클래스 (Implement Layer) 기존 계약과 수정 개념객체를 비교하여 변경된 필드 목록 생성
 */
@Component
public class ContractChangeDetector {

	public String detectChanges(InsuranceContract existing, ModifyContract mc) {
		List<String> changedFields = new ArrayList<>();

		if (mc.status() != null && !Objects.equals(mc.status(), existing.status())) {
			changedFields.add("계약상태");
		}

		detectApplicantChanges(existing.getContractor(), mc.applicant(), changedFields);
		detectPeriodChanges(existing.metaInfo().period(), mc.period(), changedFields);
		detectInsuredPeopleChanges(existing.insuredPeople(), mc.insuredPeople(), changedFields);
		detectPaymentChanges(existing.paymentInfo(), mc.payment(), changedFields);

		if (changedFields.isEmpty()) {
			return null;
		}

		return String.join(", ", changedFields) + "을(를) 변경하였습니다.";
	}

	private void detectApplicantChanges(InsuredPerson contractor, ContractApplicant applicant,
			List<String> changedFields) {
		if (applicant == null || contractor == null) {
			return;
		}

		if (applicant.name() != null && !Objects.equals(applicant.name(), contractor.name())) {
			changedFields.add("계약자명");
		}
		if (applicant.phoneNumber() != null && !Objects.equals(applicant.phoneNumber(), contractor.phone())) {
			changedFields.add("전화번호");
		}
		if (applicant.email() != null && !Objects.equals(applicant.email(), contractor.email())) {
			changedFields.add("이메일");
		}
	}

	private void detectPeriodChanges(InsurancePeriod existing, ContractPeriod period, List<String> changedFields) {
		if (period == null) {
			return;
		}

		if (period.startDate() != null && !Objects.equals(period.startDate(), existing.startDate())) {
			changedFields.add("보험시작일");
		}
		if (period.endDate() != null && !Objects.equals(period.endDate(), existing.endDate())) {
			changedFields.add("보험종료일");
		}
	}

	private void detectInsuredPeopleChanges(List<InsuredPerson> existing, List<ModifyInsuredPerson> modifications,
			List<String> changedFields) {
		if (modifications == null || modifications.isEmpty()) {
			return;
		}

		if (existing.size() != modifications.size()) {
			changedFields.add("피보험자");
			return;
		}

		boolean hasNameChange = false;
		boolean hasEnglishNameChange = false;
		boolean hasGenderChange = false;
		boolean hasPassportChange = false;
		boolean hasResidentNumberChange = false;

		for (int i = 0; i < existing.size(); i++) {
			InsuredPerson existingPerson = existing.get(i);
			ModifyInsuredPerson m = modifications.get(i);

			if (!Objects.equals(m.name(), existingPerson.name())) {
				hasNameChange = true;
			}
			if (!Objects.equals(m.englishName(), existingPerson.englishName())) {
				hasEnglishNameChange = true;
			}
			if (!Objects.equals(m.gender(), existingPerson.gender())) {
				hasGenderChange = true;
			}
			if (!Objects.equals(m.passportNumber(), existingPerson.passportNumber())) {
				hasPassportChange = true;
			}
			if (!Objects.equals(m.residentNumber(), existingPerson.residentNumber())) {
				hasResidentNumberChange = true;
			}
		}

		if (hasNameChange) {
			changedFields.add("피보험자 한글이름");
		}
		if (hasEnglishNameChange) {
			changedFields.add("피보험자 영문이름");
		}
		if (hasGenderChange) {
			changedFields.add("피보험자 성별");
		}
		if (hasPassportChange) {
			changedFields.add("피보험자 여권번호");
		}
		if (hasResidentNumberChange) {
			changedFields.add("피보험자 주민번호");
		}
	}

	private void detectPaymentChanges(PaymentInfo existing, ContractPayment payment, List<String> changedFields) {
		if (payment == null || existing == null)
			return;

		if (payment.method() != null && !Objects.equals(payment.method(), existing.method())) {
			changedFields.add("결제수단");
		}
		if (payment.canceledAt() != null && !Objects.equals(payment.canceledAt(), existing.canceledAt())) {
			changedFields.add("결제해지일");
		}
	}

}