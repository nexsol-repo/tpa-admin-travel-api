package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ContractPayment(String status, String method, BigDecimal totalAmount, LocalDateTime paidAt,
		LocalDateTime canceledAt) {
}