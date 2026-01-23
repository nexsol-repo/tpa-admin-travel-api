package com.nexsol.tpa.core.domain;

import com.nexsol.tpa.core.enums.ContractStatus;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ContractSearchCriteria(LocalDate startDate, LocalDate endDate, String partnerCode, String channelCode,
        ContractStatus status, String keywordType, String keyword

) {

    public static ContractSearchCriteria empty() {
        return new ContractSearchCriteria(null, null, null, null, null, null, null);
    }
}
