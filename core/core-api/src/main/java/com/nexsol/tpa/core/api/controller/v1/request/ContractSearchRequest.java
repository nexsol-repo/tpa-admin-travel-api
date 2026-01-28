package com.nexsol.tpa.core.api.controller.v1.request;

import com.nexsol.tpa.core.domain.contract.ContractSearchCriteria;
import com.nexsol.tpa.core.enums.ContractStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ContractSearchRequest(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,

        // [Changed] Code 대신 Name 사용 (UI 드롭다운에서 이름을 보낸다고 가정)
        String partnerName, String channelName, String insurerName,

        ContractStatus status,

        String applicantName) {
    public ContractSearchCriteria toCriteria() {
        return ContractSearchCriteria.builder()
            .startDate(startDate)
            .endDate(endDate)
            .partnerName(partnerName)
            .channelName(channelName)
            .insurerName(insurerName)
            .status(status)
            .applicantName(applicantName)
            .build();
    }
}