package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ContractStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
            .paymentInfo(updatePayment(existing.paymentInfo(), command.payment()))
            .insuredPeople(updateInsuredPeople(existing.insuredPeople(), command.insuredPeople()))
            .build();
    }

    private ContractStatus resolveStatus(InsuranceContract existing, ContractUpdateCommand command) {
        return command.status() != null ? command.status() : existing.status();
    }

    private PaymentInfo updatePayment(PaymentInfo existing, ContractUpdateCommand.PaymentUpdateCommand command) {
        if (command == null) {
            return existing;
        }

        // existing이 null인 경우(결제정보가 없는 상태에서 수정) 대비
        String currentMethod = (existing != null) ? existing.method() : null;
        BigDecimal currentAmount = (existing != null) ? existing.totalAmount() : java.math.BigDecimal.ZERO;

        return PaymentInfo.builder()
            .method(command.method() != null ? command.method() : currentMethod)
            .totalAmount(currentAmount) // 금액 변경 로직은 별도로 없다면 기존 유지
            .paidAt(command.paidAt() != null ? command.paidAt() : (existing != null ? existing.paidAt() : null))
            .canceledAt(command.canceledAt()) // 취소일은 null이 올 수 있음 (취소 철회 등 고려)
            .build();
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
