package com.nexsol.tpa.core.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 계약 변경 감지 도구 클래스 (Implement Layer) 기존 계약과 수정 명령을 비교하여 변경된 필드 목록 생성
 */
@Component
public class ContractChangeDetector {

    /**
     * 변경된 필드 목록을 한글 메시지로 반환
     * @return "성별, 한글이름을 변경하였습니다." 또는 null (변경 없음)
     */
    public String detectChanges(InsuranceContract existing, ContractUpdateCommand command) {
        List<String> changedFields = new ArrayList<>();

        // 상태 변경 감지
        if (command.status() != null && !Objects.equals(command.status(), existing.status())) {
            changedFields.add("계약상태");
        }

        // 계약자 정보 변경 감지
        detectApplicantChanges(existing.applicant(), command.applicant(), changedFields);

        // 보험기간 변경 감지
        detectPeriodChanges(existing.metaInfo().period(), command.period(), changedFields);

        // 피보험자 정보 변경 감지
        detectInsuredPeopleChanges(existing.insuredPeople(), command.insuredPeople(), changedFields);

        if (changedFields.isEmpty()) {
            return null;
        }

        return String.join(", ", changedFields) + "을(를) 변경하였습니다.";
    }

    private void detectApplicantChanges(Applicant existing, ContractUpdateCommand.ApplicantUpdateCommand command,
            List<String> changedFields) {
        if (command == null) {
            return;
        }

        if (command.name() != null && !Objects.equals(command.name(), existing.name())) {
            changedFields.add("계약자명");
        }
        if (command.phoneNumber() != null && !Objects.equals(command.phoneNumber(), existing.phoneNumber())) {
            changedFields.add("전화번호");
        }
        if (command.email() != null && !Objects.equals(command.email(), existing.email())) {
            changedFields.add("이메일");
        }
    }

    private void detectPeriodChanges(InsurancePeriod existing, ContractUpdateCommand.PeriodUpdateCommand command,
            List<String> changedFields) {
        if (command == null) {
            return;
        }

        if (command.startDate() != null && !Objects.equals(command.startDate(), existing.startDate())) {
            changedFields.add("보험시작일");
        }
        if (command.endDate() != null && !Objects.equals(command.endDate(), existing.endDate())) {
            changedFields.add("보험종료일");
        }
    }

    private void detectInsuredPeopleChanges(List<InsuredPerson> existing,
            List<ContractUpdateCommand.InsuredPersonUpdateCommand> commands, List<String> changedFields) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        // 피보험자 목록이 변경되었으면 상세 비교
        if (existing.size() != commands.size()) {
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
            ContractUpdateCommand.InsuredPersonUpdateCommand commandPerson = commands.get(i);

            if (!Objects.equals(commandPerson.name(), existingPerson.name())) {
                hasNameChange = true;
            }
            if (!Objects.equals(commandPerson.englishName(), existingPerson.englishName())) {
                hasEnglishNameChange = true;
            }
            if (!Objects.equals(commandPerson.gender(), existingPerson.gender())) {
                hasGenderChange = true;
            }
            if (!Objects.equals(commandPerson.passportNumber(), existingPerson.passportNumber())) {
                hasPassportChange = true;
            }
            if (!Objects.equals(commandPerson.residentNumber(), existingPerson.residentNumber())) {
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

}
