package com.nexsol.tpa.storage.db.core;

import com.nexsol.tpa.core.domain.applicant.Applicant;
import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.contract.ContractMeta;
import com.nexsol.tpa.core.domain.contract.InsuranceContract;
import com.nexsol.tpa.core.domain.payment.PaymentInfo;
import com.nexsol.tpa.core.domain.product.InsurancePeriod;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.domain.subscription.SubscriptionOrigin;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class TravelContractMapper {

	/**
	 * 도메인 모델 -> 엔티티 변환 (생성 시 사용)
	 */
	public TravelContractEntity toEntity(InsuranceContract contract) {
		if (contract == null) {
			return null;
		}

		TravelContractEntity entity = new TravelContractEntity();

		// 상태
		if (contract.status() != null) {
			entity.updateStatus(contract.status().name());
		}

		// 가입 출처 및 기간 정보
		if (contract.metaInfo() != null) {
			mapMetaInfo(entity, contract.metaInfo());
		}

		// 플랜 패밀리 정보
		if (contract.productPlan() != null) {
			entity.updateFamilyId(contract.productPlan().familyId());
			entity.updateCountryName(contract.productPlan().travelCountry());
			entity.updateCountryCode(contract.productPlan().countryCode());
		}

		// 결제 정보
		if (contract.paymentInfo() != null) {
			entity.updateTotalPremium(contract.paymentInfo().totalAmount());
		}

		// 담당자 ID
		entity.updateEmployeeId(contract.employeeId());

		return entity;
	}

	/**
	 * 엔티티 -> 도메인 모델 변환
	 */
	public InsuranceContract toDomain(TravelContractEntity entity, TravelPaymentEntity payment,
			TravelRefundEntity refund, List<TravelInsuredEntity> people, Map<Long, TravelInsurancePlanEntity> planMap,
			TravelInsurancePlanFamilyEntity family) {
		if (entity == null) {
			return null;
		}

		List<TravelInsuredEntity> safePeople = (people != null) ? people : Collections.emptyList();
		List<InsuredPerson> insuredPeople = safePeople.stream().map(p -> p.toDomain(planMap)).toList();

		// insuredPeople 중 isContractor=true 인 사람에서 applicant 추출
		Applicant applicant = extractApplicant(insuredPeople);

		// people의 planId로 plan 조회
		TravelInsurancePlanEntity plan = findPlanFromPeople(safePeople, planMap);

		return InsuranceContract.builder()
			.contractId(entity.getId())
			.status(determineStatus(entity, payment))
			.metaInfo(toContractMeta(entity))
			.productPlan(toProductPlan(entity, plan, family))
			.applicant(applicant)
			.paymentInfo(toPaymentInfo(entity, payment))
			.refundInfo(refund != null ? refund.toDomain() : null)
			.insuredPeople(insuredPeople)
			.employeeId(entity.getEmployeeId())
			.insuredCount(insuredPeople.size())
			.totalPremium(entity.getTotalPremium())
			.build();
	}

	public InsuranceContract toDomain(TravelContractEntity entity) {
		return toDomain(entity, null, null, Collections.emptyList(), Collections.emptyMap(), null);
	}

	private Applicant extractApplicant(List<InsuredPerson> insuredPeople) {
		return insuredPeople.stream()
			.filter(p -> Boolean.TRUE.equals(p.isContractor()))
			.findFirst()
			.map(p -> Applicant.builder()
				.name(p.name())
				.residentNumber(p.residentNumber())
				.phoneNumber(p.phone())
				.email(p.email())
				.build())
			.orElse(null);
	}

	private TravelInsurancePlanEntity findPlanFromPeople(List<TravelInsuredEntity> people,
			Map<Long, TravelInsurancePlanEntity> planMap) {
		if (planMap == null || planMap.isEmpty()) {
			return null;
		}
		return people.stream()
			.map(TravelInsuredEntity::getPlanId)
			.filter(planMap::containsKey)
			.findFirst()
			.map(planMap::get)
			.orElse(null);
	}

	private void mapMetaInfo(TravelContractEntity entity, ContractMeta meta) {
		entity.updateSubscriptionOrigin(meta.origin());
		entity.updateInsurancePeriod(meta.period());
		if (meta.policyNumber() != null) {
			entity.updatePolicyNumber(meta.policyNumber());
		}
		if (meta.policyLink() != null) {
			entity.updatePolicyLink(meta.policyLink());
		}
		if (meta.applicationDate() != null) {
			entity.updateApplyDate(meta.applicationDate());
		}
	}

	private ContractMeta toContractMeta(TravelContractEntity entity) {
		return ContractMeta.builder()
			.policyNumber(entity.getPolicyNumber())
			.policyLink(entity.getPolicyLink())
			.origin(SubscriptionOrigin.builder()
				.partnerId(entity.getPartnerId())
				.partnerName(entity.getPartnerName())
				.channelId(entity.getChannelId())
				.channelName(entity.getChannelName())
				.insurerId(entity.getInsurerId())
				.insurerName(entity.getInsurerName())
				.build())
			.applicationDate(entity.getApplyDate())
			.period(new InsurancePeriod(entity.getInsureStartDate(), entity.getInsureEndDate()))
			.build();
	}

	private ProductPlan toProductPlan(TravelContractEntity entity, TravelInsurancePlanEntity plan,
			TravelInsurancePlanFamilyEntity family) {
		if (plan == null) {
			return ProductPlan.builder()
				.familyId(entity.getFamilyId())
				.travelCountry(entity.getCountryName())
				.countryCode(entity.getCountryCode())
				.build();
		}

		String familyName = (family != null) ? family.getFamilyName() : null;
		boolean isLoss = (family != null) && family.isLoss();
		String displayPlanName = buildDisplayPlanName(familyName);

		return ProductPlan.builder()
			.planId(plan.getId())
			.familyId(entity.getFamilyId())
			.productName(plan.getProductName())
			.planName(plan.getPlanName())
			.displayPlanName(displayPlanName)
			.silsonExclude(!isLoss)
			.travelCountry(entity.getCountryName())
			.countryCode(entity.getCountryCode())
			.build();
	}

	private String buildDisplayPlanName(String familyName) {
		if (familyName == null) {
			return null;
		}
		// "딱좋은플랜B 실손제외" → "딱좋은플랜B" → "딱좋은플랜"
		// "가뿐한플랜B" → "가뿐한플랜"
		return familyName.replace(" 실손제외", "").replaceAll("[A-Z]$", "");
	}

	private PaymentInfo toPaymentInfo(TravelContractEntity entity, TravelPaymentEntity payment) {
		if (payment == null) {
			return PaymentInfo.builder()
				.totalAmount(entity.getTotalPremium() != null ? entity.getTotalPremium() : BigDecimal.ZERO)
				.build();
		}
		return payment.toDomain(entity.getTotalPremium());
	}

	private ContractStatus determineStatus(TravelContractEntity entity, TravelPaymentEntity payment) {
		if (entity.getStatus() != null) {
			try {
				return ContractStatus.valueOf(entity.getStatus());
			}
			catch (IllegalArgumentException e) {
				// Fallback
			}
		}
		return ContractStatus.COMPLETED;
	}

}