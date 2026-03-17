package com.nexsol.tpa.core.domain.plan;

import com.nexsol.tpa.core.error.CoreErrorType;
import com.nexsol.tpa.core.error.CoreException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 플랜명 + 주민번호로 적합한 플랜을 찾는 도구 클래스
 * - 0~69세: B플랜 조회 (예: "가뿐한플랜B_15~69세")
 * - 70세 이상: A플랜 + is_loss(실손 포함/제외) 조건으로 조회
 */
@Component
@RequiredArgsConstructor
public class PlanResolver {

	private static final Long AGE_GROUP_CHILD = 1L;   // 0~14세
	private static final Long AGE_GROUP_ADULT = 2L;   // 15~69세
	private static final Long AGE_GROUP_SENIOR = 3L;  // 70~80세

	private final PlanReader planReader;

	/**
	 * 플랜명과 주민번호로 적합한 플랜을 찾는다.
	 * @param planName 플랜 표시명 (예: "가뿐한플랜")
	 * @param residentNumber 주민번호 (YYMMDD-XXXXXXX 형식)
	 * @param silsonExclude 실손제외 여부 (true: 실손제외, false: 실손포함) - 70세 이상에서만 사용
	 * @return 해당 나이대의 플랜
	 */
	public Plan resolve(String planName, String residentNumber, Boolean silsonExclude) {
		int age = calculateAge(residentNumber, LocalDate.now());
		Long ageGroupId = determineAgeGroup(age);
		boolean isLoss = toIsLoss(silsonExclude);
		String planNamePrefix = buildPlanNamePrefix(planName, ageGroupId);

		return planReader.readByPlanNamePrefixAndAgeGroupIdAndIsLoss(planNamePrefix, ageGroupId, isLoss)
			.orElseThrow(() -> new CoreException(CoreErrorType.NOT_FOUND_DATA,
					"플랜을 찾을 수 없습니다: " + planName + ", 나이: " + age + ", 실손제외: " + silsonExclude));
	}

	/**
	 * 70세 이상은 A플랜, 그 외는 B플랜 prefix를 반환한다.
	 */
	private String buildPlanNamePrefix(String planName, Long ageGroupId) {
		if (isSenior(ageGroupId)) {
			return planName;
		}
		return planName + "B";
	}

	private boolean isSenior(Long ageGroupId) {
		return AGE_GROUP_SENIOR.equals(ageGroupId);
	}

	/**
	 * silsonExclude → is_loss 변환
	 * silsonExclude=true → 실손제외 → is_loss=false
	 * silsonExclude=false/null → 실손포함 → is_loss=true
	 */
	private boolean toIsLoss(Boolean silsonExclude) {
		return silsonExclude == null || !silsonExclude;
	}

	/**
	 * 주민번호에서 만나이를 계산한다.
	 * @param residentNumber 주민번호 문자열
	 * @param baseDate 기준일 (현재 날짜)
	 * @return 만나이
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