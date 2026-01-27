package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 계약 수정 명령 객체 Presentation Layer에서 Business Layer로 전달되는 개념 객체
 */
@Builder
public record ContractUpdateCommand(Long contractId, ContractStatus status, ApplicantUpdateCommand applicant,
        PeriodUpdateCommand period, List<InsuredPersonUpdateCommand> insuredPeople, String memo) {

    @Builder
    public record ApplicantUpdateCommand(String name, String phoneNumber, String email) {
    }

    @Builder
    public record PeriodUpdateCommand(LocalDateTime startDate, LocalDateTime endDate) {
    }

    @Builder
    public record InsuredPersonUpdateCommand(String name, String englishName, String residentNumber,
            String passportNumber, String gender) {
    }
}
