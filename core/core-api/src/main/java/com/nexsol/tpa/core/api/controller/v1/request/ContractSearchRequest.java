package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.ContractSearchCriteria;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ContractSearchRequest(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, String partnerCode, ContractStatus status,
        String keywordType, String keyword) {
    // Controller에서 도메인 검색 조건(Criteria)으로 변환
    public ContractSearchCriteria toCriteria() {
        return new ContractSearchCriteria(startDate, endDate, partnerCode, null, status, keywordType, keyword);
    }
}
