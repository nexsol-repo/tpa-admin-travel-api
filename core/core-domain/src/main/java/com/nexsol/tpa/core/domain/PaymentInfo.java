package com.nexsol.tpa.core.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentInfo(String method, BigDecimal totalAmount, LocalDateTime paidAt, LocalDateTime canceledAt) {
}
