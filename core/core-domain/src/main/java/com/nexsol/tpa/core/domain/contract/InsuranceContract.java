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
        Applicant applicant, PaymentInfo paymentInfo, List<InsuredPerson> insuredPeople, Long employeeId

) {
}
