package com.nexsol.tpa.core.domain.plan;

import com.nexsol.tpa.core.enums.PlanGrade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 플랜 패밀리 조회 도구 클래스 (Implement Layer) 플랜명 + 실손여부로 패밀리명을 조회한다. - 실손포함: "가뿐한플랜B" (is_loss=1)
 * - 실손제외: "가뿐한플랜B 실손제외" (is_loss=0)
 */
@Component
@RequiredArgsConstructor
public class PlanFamilyReader {

	private static final String SILSON_EXCLUDE_SUFFIX = " 실손제외";

	private final PlanFamilyRepository planFamilyRepository;

	/**
	 * 플랜 표시명과 실손여부로 패밀리명을 조회한다. (기본 등급: B)
	 */
	public Optional<String> findFamilyName(String planName, boolean isLoss) {
		return findFamilyName(planName, isLoss, PlanGrade.defaultGrade());
	}

	/**
	 * 플랜 표시명, 실손여부, 등급으로 패밀리명을 조회한다.
	 */
	public Optional<String> findFamilyName(String planName, boolean isLoss, PlanGrade grade) {
		PlanGrade resolvedGrade = (grade != null) ? grade : PlanGrade.defaultGrade();
		return planFamilyRepository.findFamilyNameByExactName(buildFamilyName(planName, isLoss, resolvedGrade), isLoss);
	}

	/**
	 * 플랜 표시명과 실손여부로 패밀리 ID를 조회한다. (기본 등급: B)
	 */
	public Optional<Long> findFamilyId(String planName, boolean isLoss) {
		return findFamilyId(planName, isLoss, PlanGrade.defaultGrade());
	}

	/**
	 * 플랜 표시명, 실손여부, 등급으로 패밀리 ID를 조회한다.
	 */
	public Optional<Long> findFamilyId(String planName, boolean isLoss, PlanGrade grade) {
		PlanGrade resolvedGrade = (grade != null) ? grade : PlanGrade.defaultGrade();
		return planFamilyRepository.findFamilyIdByExactName(buildFamilyName(planName, isLoss, resolvedGrade), isLoss);
	}

	private String buildFamilyName(String planName, boolean isLoss, PlanGrade grade) {
		String familyName = planName + grade.getSuffix();
		if (!isLoss) {
			familyName += SILSON_EXCLUDE_SUFFIX;
		}
		return familyName;
	}

}