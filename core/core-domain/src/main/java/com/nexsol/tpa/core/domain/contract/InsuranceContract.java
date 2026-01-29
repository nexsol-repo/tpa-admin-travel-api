package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.enums.ContractStatus;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.Builder;

import java.util.List;

/**
 * 여행자 보험의 Aggregate Root
 *
 */
@Builder
public record InsuranceContract(Long contractId, ContractStatus status, ContractMeta metaInfo, ProductPlan productPlan,
        Applicant applicant, PaymentInfo paymentInfo, List<InsuredPerson> insuredPeople, Long employeeId,
        Integer insuredCount

) {
    private static final int REPRESENTATIVE_COUNT = 1;

    /**
     * 총 피보험자 수 (대표 피보험자 1명 + 동반자 수)
     * - 저장 시 계산용
     */
    public int calculateTotalInsuredCount() {
        int companionCount = (insuredPeople != null) ? insuredPeople.size() : 0;
        return REPRESENTATIVE_COUNT + companionCount;
    }

    /**
     * 총 피보험자 수 조회
     * - DB에 저장된 값 우선, 없으면 계산
     */
    public int getTotalInsuredCount() {
        return (insuredCount != null) ? insuredCount : calculateTotalInsuredCount();
    }
}
