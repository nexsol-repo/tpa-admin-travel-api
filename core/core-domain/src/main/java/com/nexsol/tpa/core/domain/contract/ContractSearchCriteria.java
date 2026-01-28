package com.nexsol.tpa.core.domain.contract;

import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ContractSearchCriteria(LocalDate startDate, LocalDate endDate, String partnerName, String channelName,
        String insurerName, ContractStatus status, String applicantName) {
    public static ContractSearchCriteria empty() {
        return ContractSearchCriteria.builder().build();
    }
}