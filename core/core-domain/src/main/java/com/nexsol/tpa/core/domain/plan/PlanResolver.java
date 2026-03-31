package com.nexsol.tpa.core.domain.plan;

import com.nexsol.tpa.core.enums.PlanGrade;
import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 플랜명 + 주민번호로 적합한 플랜을 찾는 도구 클래스 1. plan_family 테이블에서 is_loss 조건으로 패밀리명 조회 (예: "가뿐한플랜B")
 * 2. 패밀리명 + age_group_id로 plan 테이블에서 실제 플랜 조회
 */
@Component
@RequiredArgsConstructor
public class PlanResolver {

	private static final Long AGE_GROUP_CHILD = 1L; // 0~14세

	private static final Long AGE_GROUP_ADULT = 2L; // 15~69세

	private static final Long AGE_GROUP_SENIOR = 3L; // 70~80세

	private final PlanReader planReader;

	private final PlanFamilyReader planFamilyReader;

	/**
	 * 플랜명과 주민번호로 적합한 플랜을 찾는다. (기본 등급: B, 기준일: 오늘)
	 */
	public Plan resolve(String planName, String residentNumber, Boolean silsonExclude) {
		return resolve(planName, residentNumber, silsonExclude, PlanGrade.defaultGrade(), LocalDate.now());
	}

	/**
	 * 플랜명, 주민번호, 등급으로 적합한 플랜을 찾는다. (기준일: 오늘)
	 */
	public Plan resolve(String planName, String residentNumber, Boolean silsonExclude, PlanGrade grade) {
		return resolve(planName, residentNumber, silsonExclude, grade, LocalDate.now());
	}

	/**
	 * 플랜명, 주민번호, 등급, 기준일로 적합한 플랜을 찾는다.
	 * @param planName 플랜 표시명 (예: "가뿐한플랜")
	 * @param residentNumber 주민번호 (YYMMDD-XXXXXXX 형식)
	 * @param silsonExclude 실손제외 여부 (true: 실손제외, false: 실손포함)
	 * @param grade 플랜 등급 (A 또는 B, null이면 기본값 B)
	 * @param baseDate 만나이 계산 기준일 (null이면 오늘)
	 * @return 해당 나이대의 플랜
	 */
	public Plan resolve(String planName, String residentNumber, Boolean silsonExclude, PlanGrade grade,
			LocalDate baseDate) {
		LocalDate resolvedBaseDate = (baseDate != null) ? baseDate : LocalDate.now();
		PlanGrade resolvedGrade = (grade != null) ? grade : PlanGrade.defaultGrade();

		int age = calculateAge(residentNumber, resolvedBaseDate);
		Long ageGroupId = determineAgeGroup(age);
		boolean isLoss = silsonExclude == null || !silsonExclude;

		Long familyId = planFamilyReader.findFamilyId(planName, isLoss, resolvedGrade)
			.orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA,
					"플랜 패밀리를 찾을 수 없습니다: " + planName + resolvedGrade.getSuffix() + ", 실손제외: " + silsonExclude));

		return planReader.readByFamilyIdAndAgeGroupId(familyId, ageGroupId)
			.orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA,
					"플랜을 찾을 수 없습니다: " + planName + resolvedGrade.getSuffix() + ", 나이: " + age));
	}

	/**
	 * 주민번호에서 만나이를 계산한다.
	 */
	int calculateAge(String residentNumber, LocalDate baseDate) {
		String cleaned = residentNumber.replace("-", "");
		if (cleaned.length() < 7) {
			throw new CoreException(CoreErrorType.INVALID_REQUEST, "주민번호 형식이 올바르지 않습니다.");
		}

		int yy = Integer.parseInt(cleaned.substring(0, 2));
		int mm = Integer.parseInt(cleaned.substring(2, 4));
		int dd = Integer.parseInt(cleaned.substring(4, 6));
		char genderCode = cleaned.charAt(6);

		int century = switch (genderCode) {
			case '1', '2', '5', '6' -> 1900;
			case '3', '4', '7', '8' -> 2000;
			default -> throw new CoreException(CoreErrorType.INVALID_REQUEST, "주민번호 성별코드가 올바르지 않습니다.");
		};

		LocalDate birthDate = LocalDate.of(century + yy, mm, dd);
		int age = baseDate.getYear() - birthDate.getYear();
		if (baseDate.getMonthValue() < mm || (baseDate.getMonthValue() == mm && baseDate.getDayOfMonth() < dd)) {
			age--;
		}
		return age;
	}

	Long determineAgeGroup(int age) {
		if (age <= 14) {
			return AGE_GROUP_CHILD;
		}
		if (age <= 69) {
			return AGE_GROUP_ADULT;
		}
		return AGE_GROUP_SENIOR;
	}

}