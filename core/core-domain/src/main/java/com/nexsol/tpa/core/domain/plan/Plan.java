package com.nexsol.tpa.core.domain.plan;

/**
 * 보험 플랜 도메인 모델
 */
public record Plan(Long id, String code, String name, String productName, String fullName, Long insurerId) {

}
