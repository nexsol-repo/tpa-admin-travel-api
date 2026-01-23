package com.nexsol.tpa.core.domain;

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
        Applicant applicant, PaymentInfo paymentInfo, List<InsuredPerson> insuredPeople

) {
    public InsuranceContract {
        if (insuredPeople == null || insuredPeople.isEmpty()) {
            throw new CoreException(CoreErrorType.INSURANCE_NOTFOUND_PEOPLE_DATA);
        }
    }
}
