package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.domain.applicant.InsuredPerson;
import com.nexsol.tpa.core.domain.plan.Plan;
import com.nexsol.tpa.core.domain.plan.PlanReader;
import com.nexsol.tpa.core.domain.plan.PlanResolver;
import com.nexsol.tpa.core.domain.product.ProductPlan;
import com.nexsol.tpa.core.enums.PlanGrade;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 계약의 플랜 관련 수정을 담당하는 도구 클래스 (Implement Layer) - 계약 대표 플랜 resolve - 피보험자별 개별 플랜 resolve
 */
@Component
@RequiredArgsConstructor
public class ContractPlanUpdater {

	private final PlanReader planReader;

	private final PlanResolver planResolver;

	/**
	 * 계약의 대표 플랜을 수정한다.
	 */
	public ProductPlan updateProductPlan(InsuranceContract existing, ModifyContract mc) {
		ProductPlan existingPlan = existing.productPlan();
		PlanSelection ps = mc.plan();

		if (ps == null) {
			return existingPlan;
		}

		if (!hasPlanChange(ps)) {
			return applyTravelInfoOnly(existingPlan, ps);
		}

		Plan resolvedPlan = resolvePlanFromContext(existing, mc);
		if (resolvedPlan != null) {
			return buildProductPlan(resolvedPlan, existingPlan, ps);
		}

		return applyTravelInfoOnly(existingPlan, ps);
	}

	/**
	 * 플랜 변경 시 각 피보험자의 주민번호(나이)에 맞는 개별 플랜을 resolve하여 planId를 갱신한다.
	 */
	public List<InsuredPerson> resolveInsuredPeoplePlan(List<InsuredPerson> insuredPeople, PlanSelection ps,
			LocalDateTime applicationDate) {
		if (insuredPeople == null || insuredPeople.isEmpty()) {
			return insuredPeople;
		}

		LocalDate baseDate = toLocalDate(applicationDate);

		return insuredPeople.stream().map(person -> {
			if (person.residentNumber() == null) {
				return person;
			}
			Plan resolved = planResolver.resolve(ps.planName(), person.residentNumber(), ps.silsonExclude(),
					ps.planGrade(), baseDate);
			return rebuildWithPlanId(person, resolved.id());
		}).toList();
	}

	private boolean hasPlanChange(PlanSelection ps) {
		return ps.planName() != null || ps.planId() != null;
	}

	private Plan resolvePlanFromContext(InsuranceContract existing, ModifyContract mc) {
		PlanSelection ps = mc.plan();
		LocalDate baseDate = resolveBaseDate(existing, mc);

		if (ps.planName() != null) {
			String residentNumber = resolveResidentNumber(existing, mc);
			if (residentNumber != null) {
				return planResolver.resolve(ps.planName(), residentNumber, ps.silsonExclude(), ps.planGrade(),
						baseDate);
			}
		}

		if (ps.planId() != null) {
			return planReader.read(ps.planId()).orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA));
		}

		return null;
	}

	private String resolveResidentNumber(InsuranceContract existing, ModifyContract mc) {
		if (mc.applicant() != null && mc.applicant().residentNumber() != null) {
			return mc.applicant().residentNumber();
		}
		if (existing.getContractor() != null && existing.getContractor().residentNumber() != null) {
			return existing.getContractor().residentNumber();
		}
		return null;
	}

	private LocalDate resolveBaseDate(InsuranceContract existing, ModifyContract mc) {
		if (mc.applicationDate() != null) {
			return mc.applicationDate().toLocalDate();
		}
		if (existing.metaInfo() != null && existing.metaInfo().applicationDate() != null) {
			return existing.metaInfo().applicationDate().toLocalDate();
		}
		return LocalDate.now();
	}

	private ProductPlan buildProductPlan(Plan plan, ProductPlan existingPlan, PlanSelection ps) {
		String travelCountry = existingPlan.travelCountry();
		if (ps.travelCountry() != null) {
			travelCountry = ps.travelCountry();
		}

		String countryCode = existingPlan.countryCode();
		if (ps.countryCode() != null) {
			countryCode = ps.countryCode();
		}

		return ProductPlan.builder()
			.planId(plan.id())
			.productName(plan.fullName())
			.planName(plan.name())
			.travelCountry(travelCountry)
			.countryCode(countryCode)
			.coverageLink(existingPlan.coverageLink())
			.build();
	}

	private ProductPlan applyTravelInfoOnly(ProductPlan existingPlan, PlanSelection ps) {
		String travelCountry = existingPlan.travelCountry();
		if (ps.travelCountry() != null) {
			travelCountry = ps.travelCountry();
		}

		String countryCode = existingPlan.countryCode();
		if (ps.countryCode() != null) {
			countryCode = ps.countryCode();
		}

		if (travelCountry.equals(existingPlan.travelCountry()) && countryCode.equals(existingPlan.countryCode())) {
			return existingPlan;
		}

		return ProductPlan.builder()
			.planId(existingPlan.planId())
			.productName(existingPlan.productName())
			.planName(existingPlan.planName())
			.travelCountry(travelCountry)
			.countryCode(countryCode)
			.coverageLink(existingPlan.coverageLink())
			.build();
	}

	private InsuredPerson rebuildWithPlanId(InsuredPerson person, Long planId) {
		return InsuredPerson.builder()
			.id(person.id())
			.planId(planId)
			.isContractor(person.isContractor())
			.name(person.name())
			.englishName(person.englishName())
			.residentNumber(person.residentNumber())
			.passportNumber(person.passportNumber())
			.gender(person.gender())
			.phone(person.phone())
			.email(person.email())
			.individualPremium(person.individualPremium())
			.individualPolicyNumber(person.individualPolicyNumber())
			.build();
	}

	private LocalDate toLocalDate(LocalDateTime dateTime) {
		if (dateTime == null) {
			return LocalDate.now();
		}
		return dateTime.toLocalDate();
	}

}