package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 계약 수정 도구 클래스 (Implement Layer) 수정 로직의 상세 구현을 담당
 */
@Component
@RequiredArgsConstructor
public class ContractUpdater {

    private final ContractRepository contractRepository;

    public InsuranceContract update(ContractUpdateCommand command) {
        InsuranceContract existing = contractRepository.findById(command.contractId())
            .orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));

        InsuranceContract updated = applyChanges(existing, command);

        return contractRepository.save(updated);
    }

    private InsuranceContract applyChanges(InsuranceContract existing, ContractUpdateCommand command) {
        return InsuranceContract.builder()
            .contractId(existing.contractId())
            .status(resolveStatus(existing, command))
            .metaInfo(updateMeta(existing.metaInfo(), command))
            .productPlan(existing.productPlan())
            .applicant(updateApplicant(existing.applicant(), command.applicant()))
            .paymentInfo(existing.paymentInfo())
            .insuredPeople(updateInsuredPeople(existing.insuredPeople(), command.insuredPeople()))
            .build();
    }

    private com.nexsol.tpa.core.enums.ContractStatus resolveStatus(InsuranceContract existing,
            ContractUpdateCommand command) {
        return command.status() != null ? command.status() : existing.status();
    }

    private ContractMeta updateMeta(ContractMeta existing, ContractUpdateCommand command) {
        if (command.period() == null) {
            return existing;
        }

        InsurancePeriod updatedPeriod = InsurancePeriod.builder()
            .startDate(
                    command.period().startDate() != null ? command.period().startDate() : existing.period().startDate())
            .endDate(command.period().endDate() != null ? command.period().endDate() : existing.period().endDate())
            .build();

        return ContractMeta.builder()
            .policyNumber(existing.policyNumber())
            .origin(existing.origin())
            .applicationDate(existing.applicationDate())
            .period(updatedPeriod)
            .build();
    }

    private Applicant updateApplicant(Applicant existing, ContractUpdateCommand.ApplicantUpdateCommand command) {
        if (command == null) {
            return existing;
        }

        return Applicant.builder()
            .name(command.name() != null ? command.name() : existing.name())
            .residentNumber(existing.residentNumber())
            .phoneNumber(command.phoneNumber() != null ? command.phoneNumber() : existing.phoneNumber())
            .email(command.email() != null ? command.email() : existing.email())
            .build();
    }

    private List<InsuredPerson> updateInsuredPeople(List<InsuredPerson> existing,
            List<ContractUpdateCommand.InsuredPersonUpdateCommand> commands) {

        if (commands == null || commands.isEmpty()) {
            return existing;
        }

        return commands.stream().map(this::toInsuredPerson).toList();
    }

    private InsuredPerson toInsuredPerson(ContractUpdateCommand.InsuredPersonUpdateCommand command) {
        return InsuredPerson.builder()
            .name(command.name())
            .englishName(command.englishName())
            .residentNumber(command.residentNumber())
            .passportNumber(command.passportNumber())
            .gender(command.gender())
            .build();
    }

}
