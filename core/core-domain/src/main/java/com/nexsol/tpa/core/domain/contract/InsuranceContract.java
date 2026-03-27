package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.payment.RefundInfo;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 여행자 보험의 Aggregate Root
 */
@Builder
public record InsuranceContract(Long contractId, ContractStatus status, ContractMeta metaInfo, ProductPlan productPlan,
		Applicant applicant, PaymentInfo paymentInfo, RefundInfo refundInfo, List<InsuredPerson> insuredPeople,
		Long employeeId, Integer insuredCount, BigDecimal totalPremium

) {

	/**
	 * 총 피보험자 수 - insuredPeople에 대표계약자도 포함되어 있음
	 */
	public int getTotalInsuredCount() {
		return (insuredPeople != null) ? insuredPeople.size() : 0;
	}

	/**
	 * insuredPeople 중 대표계약자(isContractor=true) 추출
	 */
	public InsuredPerson getContractor() {
		if (insuredPeople == null) {
			return null;
		}
		return insuredPeople.stream().filter(p -> Boolean.TRUE.equals(p.isContractor())).findFirst().orElse(null);
	}

	/**
	 * insuredPeople 중 동반자(isContractor=false) 목록
	 */
	public List<InsuredPerson> getCompanions() {
		if (insuredPeople == null) {
			return Collections.emptyList();
		}
		return insuredPeople.stream().filter(p -> !Boolean.TRUE.equals(p.isContractor())).toList();
	}
}
