package com.nexsol.tpa.core.domain.contract;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ContractRefund(BigDecimal refundAmount, String refundMethod, String bankName, String accountNumber,
		String depositorName, String refundReason, LocalDateTime refundedAt) {
}