package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.applicant.InsuredPeopleUpdater;
import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.plan.Plan;
import com.nexsol.tpa.core.domain.plan.PlanReader;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
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

    private final PlanReader planReader;

    private final InsuredPeopleUpdater insuredPeopleUpdater;

    public Long update(ContractUpdateCommand command) {
        InsuranceContract existing = contractRepository.findById(command.contractId())
            .orElseThrow(() -> new CoreException(CoreErrorType.INSURANCE_NOT_FOUND_DATA));

        InsuranceContract updated = applyChanges(existing, command);

        Long contractId = contractRepository.save(updated);

        // 동반자 수정은 별도 도구 클래스에 위임
        insuredPeopleUpdater.update(contractId, updated.insuredPeople());

        return contractId;
    }

    private InsuranceContract applyChanges(InsuranceContract existing, ContractUpdateCommand command) {
        return InsuranceContract.builder()
            .contractId(existing.contractId())
            .status(resolveStatus(existing, command))
            .metaInfo(updateMeta(existing.metaInfo(), command))
            .productPlan(updateProductPlan(existing.productPlan(), command.planId()))
            .applicant(updateApplicant(existing.applicant(), command.applicant()))
            .paymentInfo(updatePayment(existing.paymentInfo(), command.payment()))
            .insuredPeople(updateInsuredPeople(existing.insuredPeople(), command.insuredPeople()))
            .employeeId(command.employeeId() != null ? command.employeeId() : existing.employeeId())
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
        InsurancePeriod updatedPeriod = existing.period();
        if (command.period() != null) {
            updatedPeriod = InsurancePeriod.builder()
                .startDate(command.period().startDate() != null ? command.period().startDate()
                        : existing.period().startDate())
                .endDate(command.period().endDate() != null ? command.period().endDate() : existing.period().endDate())
                .build();
        }

        SubscriptionOrigin updatedOrigin = updateSubscriptionOrigin(existing.origin(), command.subscriptionOrigin());

        return ContractMeta.builder()
            .policyNumber(existing.policyNumber())
            .origin(updatedOrigin)
            .applicationDate(command.applicationDate() != null ? command.applicationDate() : existing.applicationDate())
            .period(updatedPeriod)
            .build();
    }

    /**
     * 가입 출처 정보 수정 (보험사, 채널, 제휴사 - id와 name 함께 수정)
     */
    private SubscriptionOrigin updateSubscriptionOrigin(SubscriptionOrigin existing,
            ContractUpdateCommand.SubscriptionOriginUpdateCommand command) {
        if (command == null) {
            return existing;
        }

        return SubscriptionOrigin.builder()
            .insurerId(command.insurerId() != null ? command.insurerId() : existing.insurerId())
            .insurerName(command.insurerName() != null ? command.insurerName() : existing.insurerName())
            .insurerCode(existing.insurerCode())
            .channelId(command.channelId() != null ? command.channelId() : existing.channelId())
            .channelName(command.channelName() != null ? command.channelName() : existing.channelName())
            .channelCode(existing.channelCode())
            .partnerId(command.partnerId() != null ? command.partnerId() : existing.partnerId())
            .partnerName(command.partnerName() != null ? command.partnerName() : existing.partnerName())
            .partnerCode(existing.partnerCode())
            .build();
    }

    /**
     * 플랜 정보 수정 (planId로 정보 재조회)
     */
    private ProductPlan updateProductPlan(ProductPlan existing, Long planId) {
        if (planId == null) {
            return existing;
        }

        Plan plan = planReader.read(planId).orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));

        return ProductPlan.builder()
            .planId(plan.id())
            .productName(plan.fullName())
            .planName(plan.name())
            .travelCountry(existing.travelCountry())
            .coverageLink(existing.coverageLink())
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

        // null이면 기존 유지 (수정 안 함)
        if (commands == null) {
            return existing;
        }

        // 빈 리스트면 빈 리스트 반환 (모든 동반자 삭제)
        if (commands.isEmpty()) {
            return List.of();
        }

        return commands.stream().map(this::toInsuredPerson).toList();
    }

    private InsuredPerson toInsuredPerson(ContractUpdateCommand.InsuredPersonUpdateCommand command) {
        return InsuredPerson.builder()
            .id(command.id())
            .name(command.name())
            .englishName(command.englishName())
            .residentNumber(command.residentNumber())
            .passportNumber(command.passportNumber())
            .individualPolicyNumber(command.policyNumber())
            .gender(command.gender())
            .individualPremium(command.premium())
            .build();
    }

}
