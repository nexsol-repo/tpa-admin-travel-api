package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.ContractUpdateCommand;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public record ContractUpdateRequest(ContractStatus status, ApplicantRequest applicant, PeriodRequest period,
        List<InsuredPersonRequest> insuredPeople) {

    public record ApplicantRequest(String name, String phoneNumber, String email) {
        public ContractUpdateCommand.ApplicantUpdateCommand toCommand() {
            return ContractUpdateCommand.ApplicantUpdateCommand.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .build();
        }
    }

    public record PeriodRequest(@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate) {
        public ContractUpdateCommand.PeriodUpdateCommand toCommand() {
            return ContractUpdateCommand.PeriodUpdateCommand.builder().startDate(startDate).endDate(endDate).build();
        }
    }

    public record InsuredPersonRequest(String name, String englishName, String residentNumber, String passportNumber,
            String gender) {
        public ContractUpdateCommand.InsuredPersonUpdateCommand toCommand() {
            return ContractUpdateCommand.InsuredPersonUpdateCommand.builder()
                .name(name)
                .englishName(englishName)
                .residentNumber(residentNumber)
                .passportNumber(passportNumber)
                .gender(gender)
                .build();
        }
    }

    public ContractUpdateCommand toCommand(Long contractId) {
        return ContractUpdateCommand.builder()
            .contractId(contractId)
            .status(status)
            .applicant(applicant != null ? applicant.toCommand() : null)
            .period(period != null ? period.toCommand() : null)
            .insuredPeople(
                    insuredPeople != null ? insuredPeople.stream().map(InsuredPersonRequest::toCommand).toList() : null)
            .build();
    }
}
