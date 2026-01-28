package com.nexsol.tpa.core.domain.insurer;

/**
 * 보험사 도메인 모델
 */
public record Insurer(
        Long id,
        String code,
        String name
) {

}
